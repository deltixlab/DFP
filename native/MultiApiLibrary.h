#ifndef _MULTIAPI_LIBRARY_H_
#define _MULTIAPI_LIBRARY_H_
#ifndef MULTIAPI_HEADER
#error define MULTIAPI_HEADER macro, should contain path to your header file which should not have include guard!
#endif

#ifndef JAVA_NAMESPACE
#error define JAVA_NAMESPACE macro !!!
#endif

#if defined(_MULTIAPI_) || defined(_CONCAT5_) || defined(BOOL) || defined (WORD) || defined(FN) || defined (ARGS) || defined (NO_ARGS) || defined(MULTIAPI_DOTNET)
#error _MULTIAPI_,_CONCAT5_,BOOL,WORD,FN,ARGS,MULTIAPI_DOTNET macros should not be defined!
#endif

#ifdef _WIN64
#define _MULTIAPI_(x) _declspec(dllexport) x __fastcall
#elif _WIN32
#define _MULTIAPI_(x) _declspec(dllexport) x __stdcall
#else
#define _MULTIAPI_(x) x __attribute__ ((visibility("default")))
#endif

#if defined(_WIN32)

#ifndef WINAPI
#define WINAPI __stdcall
#endif

// Assuming these typedefs:
//typedef int BOOL;
//typedef unsigned DWORD;

//
// DLL Entry Point
//
int WINAPI DllMain(void *instance, unsigned reason, void *reserved)
{
    return 1;
}

#endif /* #if defined(_WIN32) || defined(_WIN64) */


#define _CONCAT5_(a,b,c,d,e) a ## b ## c ## d ## e
#define _MULTIAPI_JAVA_(return_type, prefix, ns, name) _MULTIAPI_(return_type) _CONCAT5_(prefix,_,ns,_,name)

#define FN(return_type, name) _MULTIAPI_(return_type) name
// Purely for compatibility with Java version
#define ARGS(...) (__VA_ARGS__)
#define NO_ARGS ()
#undef MULTIAPI_DOTNET
#define MULTIAPI_DOTNET 1

/* Create .NET implementation */
#include MULTIAPI_HEADER
#undef FN
#undef MULTIAPI_DOTNET

#define FN(return_type, name) _MULTIAPI_JAVA_(return_type,JavaCritical,JAVA_NAMESPACE,name)

/* Create JavaCritical JNI implementation */
#include MULTIAPI_HEADER

#undef FN
#define FN(return_type, name) _MULTIAPI_JAVA_(return_type,Java,JAVA_NAMESPACE,name)
#undef NO_ARGS
#define NO_ARGS (void *env, void *obj)
#undef ARGS
#define ARGS(...) (void *env, void *obj, __VA_ARGS__)

/* Create Java Native Interface implementation */
#include MULTIAPI_HEADER
#undef FN
#undef ARGS
#undef NO_ARGS
#undef _MULTIAPI_
#undef _CONCAT5_

#endif

