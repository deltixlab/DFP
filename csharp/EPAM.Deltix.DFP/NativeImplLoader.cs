using RTMath.Utilities;
using System;
using System.IO;
using System.Runtime.InteropServices;

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

		internal static string GetArchName()
		{
#if NET40
			return ResourceLoader.OS.Is64 ? "amd64" : "x86";
#endif
#if NETSTANDARD2_0
			switch (RuntimeInformation.ProcessArchitecture)
			{
				case Architecture.X86:
					return "x86";
				case Architecture.X64:
					return "amd64";
				case Architecture.Arm:
					return "arm";
				case Architecture.Arm64:
					return "aarch64";
				default:
					throw new SystemException("Unsupported architecture (=" + RuntimeInformation.ProcessArchitecture + ").");
			}
#endif
		}
	}
}
