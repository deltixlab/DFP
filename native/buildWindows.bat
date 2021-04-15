mkdir buildWin32
cd buildWin32
cmake -G "Visual Studio 16 2019" -A Win32 -DAPI_PREFIX=%1 ../
MSBuild /p:PlatformToolset=ClangCL /p:Platform=Win32 /p:Configuration=Release /t:Rebuild ./native.sln
mkdir ..\bin\Release\Windows\Win32
copy /y Release\*.dll ..\bin\Release\Windows\Win32

cd ..

mkdir buildx64
cd buildx64
cmake -G "Visual Studio 16 2019" -A x64 -DAPI_PREFIX=%1 ../
MSBuild /p:PlatformToolset=ClangCL /p:Platform=x64 /p:Configuration=Release /t:Rebuild ./native.sln
mkdir ..\bin\Release\Windows\x64
copy /y Release\*.dll ..\bin\Release\Windows\x64
cd ..
