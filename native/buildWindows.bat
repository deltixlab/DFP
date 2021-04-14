mkdir buildWin32
cd buildWin32
cmake -G "Visual Studio 16 2019" -A Win32 -DAPI_PREFIX=%1 ../
MSBuild /p:PlatformToolset=ClangCL /p:Platform=Win32 /p:Configuration=Release /t:Rebuild ./native.sln
cd ..
mkdir buildWin64
cd buildWin64
cmake -G "Visual Studio 16 2019" -A x64 -DAPI_PREFIX=%1 ../
MSBuild /p:PlatformToolset=ClangCL /p:Platform=x64 /p:Configuration=Release /t:Rebuild ./native.sln
