#tool nuget:?package=NUnit.ConsoleRunner&version=3.7.0
#addin "Cake.FileHelpers"
#addin "Cake.Incubator"

//////////////////////////////////////////////////////////////////////
// ARGUMENTS
//////////////////////////////////////////////////////////////////////

var target = Argument("target", "Default");
var configuration = Argument("configuration", "Release");


//////////////////////////////////////////////////////////////////////
// PREPARATION
//////////////////////////////////////////////////////////////////////

// Define directories.
var csDir = ".";
var gradleRootDir = "..";
var nativeProjectDir = "../native";

var baseProjectName = "Deltix.DFP";
var mainProjectName = baseProjectName;
var testProjectName = $"{baseProjectName}.Test";

var nativeProjectName = "DecimalNative";
var nativeLibName = nativeProjectName;
// Reserve a few characters to later replace with actual native lib internal version number
var nativeLibLinuxFakeName = nativeLibName + "@@@@";

var nativeBinDir = $"{nativeProjectDir}/bin";
var slnPath = $"{csDir}/Deltix.DFP.sln";

// Parse version from gradle.properties
var gradleProperties = new Dictionary<String, String>();
foreach (var row in System.IO.File.ReadAllLines($"{gradleRootDir}/gradle.properties"))
    gradleProperties.Add(row.Split('=')[0], String.Join("=",row.Split('=').Skip(1).ToArray()));

var version = gradleProperties["version"];
var index = version.IndexOf("-");
var dotNetVersion = (index > 0 ? version.Substring(0, index) : version) + ".0";

//////////////////////////////////////////////////////////////////////
// Helpers
//////////////////////////////////////////////////////////////////////

String prjDir(String name) { return $"{csDir}/{name}"; }
String prjPath(String name) { return $"{prjDir(name)}/{name}.csproj"; }
String binDir(String name) { return $"{prjDir(name)}/bin/{configuration}"; }
String objDir(String name) { return $"{prjDir(name)}/obj/{configuration}"; }

void echo(string s) { Console.WriteLine(s); }

// rm -rf <dir>
void DeleteDir(string dir)
{
	if (DirectoryExists(dir))
		DeleteDirectory(new DirectoryPath(dir), new DeleteDirectorySettings { Recursive = true, Force = true });
}

// Reserve some code in case we want later to use version from .properties for the native artifacts
//var versionDashed = dotNetVersion.Replace(".", "-");

//////////////////////////////////////////////////////////////////////
// TASKS
//////////////////////////////////////////////////////////////////////

Task("CleanNativeLibs")
    .Does(() =>
{
	//DeleteDir($"{nativeBinDir}");
	DeleteDir($"{nativeProjectDir}/obj");
});

Task("Clean")
    .Does(() =>
{
    DotNetCoreClean(slnPath,
        new DotNetCoreCleanSettings { Configuration = configuration }
    );
});


Task("Restore-NuGet-Packages")
    .IsDependentOn("Clean")
    .Does(() =>
{
    DotNetCoreRestore(slnPath);
});


void BuildNativeTarget(int arch, bool isWindows)
{
    arch = 32 == arch && isWindows ? 86 : arch;
    StartProcess(isWindows ? "MSBuild" : "sh",
        new ProcessSettings { Arguments =
            isWindows ? $"/p:TargetName={nativeLibName} /p:Platform=x{arch} /p:Configuration={configuration} /t:Rebuild /m:4 {nativeProjectName}.vcxproj"
                : $"build-linux.sh {nativeLibLinuxFakeName} {configuration} {arch} {(32 == arch ? "ia32" : "intel64")}",
            WorkingDirectory= nativeProjectDir
		});
    // Possible other MSBuild options
    //  "/t:Rebuild"
    // + "/property:TargetName=" + nativeLibName + // For MSBuild
}

void BuildNativeLibs(bool isWindows)
{
    foreach (var arch in new int[] {32, 64})
        BuildNativeTarget(arch, isWindows);
}

Task("BuildNativeLinuxLibs")
    .IsDependentOn("CleanNativeLibs")
    .Does(() =>
{
	BuildNativeLibs(false);
});

Task("BuildNativeWindowsLibs")
    .IsDependentOn("CleanNativeLibs")
    .Does(() =>
{
    BuildNativeLibs(true);
});

Task("CompressNativeLibs")
    .Does(() =>
{
    var path = $"{nativeBinDir}/Release";
    StartProcess("zstd", $"-19 --rm -r {path}");
    // dotnet resources compilation workaround & versioning for linux
    foreach (var arch in new int[]{32, 64})
    {
        var fname = $"{path}/Linux/{arch}/lib{nativeLibLinuxFakeName}.so.zst";
        if (FileExists(fname))
            MoveFile(fname, fname.Replace($"{nativeLibLinuxFakeName}.so.zst", $"{nativeLibName}_so.zst"));
    }
});

Task("BuildAndCompressNativeWindowsLibs")
    .IsDependentOn("BuildNativeWindowsLibs")
    .IsDependentOn("CompressNativeLibs")
    .Does(() => {});

Task("BuildAndCompressNativeLinuxLibs")
    .IsDependentOn("BuildNativeLinuxLibs")
    .IsDependentOn("CompressNativeLibs")
    .Does(() => { });

DotNetCoreMSBuildSettings getMSBuildSettings()
 {
    return new DotNetCoreMSBuildSettings()
            .WithProperty("Version", version)
            .WithProperty("FileVersion", dotNetVersion)
            .WithProperty("AssemblyVersion", dotNetVersion);
 }

Task("Build")
    .IsDependentOn("Restore-NuGet-Packages")
    .Does(() =>
{
    var buildSettings = new DotNetCoreBuildSettings {
        Configuration = configuration,
        NoRestore = true,
        NoDependencies = true,
        MSBuildSettings = getMSBuildSettings()
    };

    if (!IsRunningOnWindows())
        buildSettings.Framework = "netstandard2.0";

    DotNetCoreBuild(prjPath(baseProjectName), buildSettings);

    if (!IsRunningOnWindows())
        buildSettings.Framework = "netcoreapp2.0";

    DotNetCoreBuild(prjPath($"{baseProjectName}.Benchmark"), buildSettings);
    DotNetCoreBuild(prjPath($"{baseProjectName}.Demo"), buildSettings);
    DotNetCoreBuild(prjPath(testProjectName), buildSettings);
});

Task("Run-Unit-Tests")
    .IsDependentOn("Build")
    .Does(() =>
{
    var buildSettings = new DotNetCoreTestSettings()
    {
        Configuration = configuration,
        NoRestore = true,
		NoBuild = true
    };

    //settings.NoBuild = true;
    if (!IsRunningOnWindows())
         buildSettings.Framework = "netcoreapp2.0";

	Information("Running tests with .NET Core 2.0");
	DotNetCoreTest(prjPath(testProjectName), buildSettings);

    // Prevent NUnit tests from running on platforms without .NET 4.0
    var glob = $"{binDir(testProjectName)}/net40/{testProjectName}.exe";
    if (IsRunningOnWindows() && GetFiles(glob).Count > 0)
    {
		Information("Running tests with NUnit & .NET Framework 4.0");
		NUnit3(glob);
    }
});

Task("Pack")
    .IsDependentOn("Build")
    .Does(() =>
{
    var settings = new DotNetCorePackSettings
    {
        Configuration = configuration,
        OutputDirectory = $"{csDir}/artifacts/",
        MSBuildSettings = getMSBuildSettings()
    };
    DotNetCorePack(".", settings);
});

Task("Push")
    .IsDependentOn("Pack")
    .Does(() =>
{
    // Task disabled
    var url = "";
    var apiKey = "";
    foreach (var file in GetFiles($"{csDir}/artifacts/*.nupkg"))
    {
        DotNetCoreTool(".", "nuget", "push " + file.FullPath + " --source " + url + " --api-key " + apiKey);
    }
});

//////////////////////////////////////////////////////////////////////
// TASK TARGETS
//////////////////////////////////////////////////////////////////////

Task("Default")
    .IsDependentOn("Run-Unit-Tests");

//////////////////////////////////////////////////////////////////////
// EXECUTION
//////////////////////////////////////////////////////////////////////

RunTarget(target);
