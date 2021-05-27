using RTMath.Utilities;
using System;
using System.IO;
using System.Runtime.InteropServices;
#if NET40
using System.Diagnostics;
using System.Linq;
using System.Text.RegularExpressions;
#endif

namespace EPAM.Deltix.DFP
{
	internal static class NativeImplLoader
	{
		internal static bool isLoaded = false;
		internal static object isLoadedLock = new object();
		internal static void Load()
		{
			if (isLoaded)
				return;
			lock (isLoadedLock)
			{
				if (isLoaded)
					return;
				var archName = GetArchName();

				var loader = ResourceLoader
					.From($"EPAM.Deltix.DFP.{ResourceLoader.OS.Name}.{archName}.*")
					.To(Path.Combine(Path.GetTempPath(), "EPAM.Deltix", "DFP", ResourceLoader.OS.AssemblyVersion, archName))
					// .LowercasePathOnLinux(false)
					.TryRandomFallbackSubDirectory(true)
					.Load();

				isLoaded = true;
			}
		}

		const string ARCH_AMD64 = "amd64";
		const string ARCH_X86 = "x86";
		const string ARCH_ARM = "arm";
		const string ARCH_AARCH64 = "aarch64";

		internal static string GetArchName()
		{
#if NET40
			if (ResourceLoader.OS.IsWindows)
			{
				return ResourceLoader.OS.Is64 ? ARCH_AMD64 : ARCH_X86;
			}
			if (ResourceLoader.OS.IsUnix)
			{
				return LinuxArchFromFile();
			}
			if (ResourceLoader.OS.IsOsx)
			{
				return ARCH_AMD64;
			}
			throw new SystemException("Can't detect operation system.");
#endif
#if NETSTANDARD2_0
			switch (RuntimeInformation.ProcessArchitecture)
			{
				case Architecture.X86:
					return ARCH_X86;
				case Architecture.X64:
					return ARCH_AMD64;
				case Architecture.Arm:
					return ARCH_ARM;
				case Architecture.Arm64:
					return ARCH_AARCH64;
				default:
					throw new SystemException("Unsupported architecture (=" + RuntimeInformation.ProcessArchitecture + ").");
			}
#endif
		}

#if NET40
		static string LinuxArchFromFile()
		{
			Process p = new Process();
			//cmd.StartInfo.FileName = "file ";
			p.StartInfo.FileName = "file";
			p.StartInfo.Arguments = "/usr/bin/file"; // "$(which file)";
			p.StartInfo.RedirectStandardInput = false;
			p.StartInfo.RedirectStandardOutput = true;
			p.StartInfo.CreateNoWindow = true;
			p.StartInfo.UseShellExecute = false;
			p.Start();

			p.WaitForExit();
			var fileOut = p.StandardOutput.ReadToEnd();

			Regex regex = new Regex(@"\: ELF (\d+)-bit.*?, (.+?), ");
			Match match = regex.Match(fileOut);
			if (!match.Success)
				throw new SystemException("Can't determine system architecture from file(=" + fileOut + ").");

			string fileArch = match.Groups[2].Value;

			if (fileArch.Contains("aarch64"))
				return ARCH_AARCH64;

			if (fileArch.Contains("ARM"))
				return ARCH_ARM;

			string[] amd64Alias = new string[] { "x86-64", "x86_64", "AMD64", "Intel 64", "Intel64", "EM64T", "x64" };
			if (amd64Alias.Any(a => a.Equals(fileArch)))
				return ARCH_AMD64;

			string[] x86Suffix = new string[] { "386", "586", "686" };
			if (x86Suffix.Any(s => fileArch.Contains(s)))
				return ARCH_X86;

			throw new SystemException("Unsupported architecture string(=" + fileArch + ").");
		}
#endif
	}
}
