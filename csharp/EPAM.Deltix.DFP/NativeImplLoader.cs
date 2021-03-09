using RTMath.Utilities;
using System.IO;

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
				var archName = ResourceLoader.OS.Is64 ? "x64" : "x32";

				var loader = ResourceLoader
					.From($"EPAM.Deltix.DFP.{ResourceLoader.OS.Name}.{archName}.*")
					.To(Path.Combine(Path.GetTempPath(), "EPAM.Deltix", "DFP", ResourceLoader.OS.AssemblyVersion, archName))
					.LowercasePathOnLinux(false)
					.TryRandomFallbackSubDirectory(true)
					.Load();

				isLoaded = true;
			}
		}
	}
}
