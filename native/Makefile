ProjectName = DecimalNative
ProjectType = SharedLibrary
SourceFiles = \
	NativeImpl.c
HeaderFiles =
SourceDirectory =

LIBS = -ldecimal

CC = icc
Configuration ?= Debug
Architecture ?= 64

OSNAME :=
ifeq ($(OS),Windows_NT)
	OSNAME := Windows
else
	UNAME_S := $(shell uname -s)
ifeq ($(UNAME_S),Linux)
	OSNAME := Linux
endif
ifeq ($(UNAME_S),Darwin)
	OSNAME := OSX
endif
endif

RootDirectory = ./
BinaryDirectory ?= $(RootDirectory)/bin/$(Configuration)/$(OSNAME)/$(Architecture)/
ObjectDirectory ?= $(RootDirectory)/obj/$(Configuration)/$(OSNAME)/$(Architecture)/

ObjectFiles = $(patsubst %.c,%.o,$(patsubst %.cxx,%.o,$(patsubst %.cpp,%.o,$(SourceFiles))))

ifeq ($(ProjectType),Application)
	ProjectTargetName = $(ProjectName)
endif

ifeq ($(ProjectType),StaticLibrary)
	ProjectTargetName = lib$(ProjectName).a
endif

ifeq ($(ProjectType),SharedLibrary)
ifeq ($(OSNAME),Linux)
	ProjectTargetName = lib$(ProjectName).so
endif
ifeq ($(OSNAME),OSX)
	ProjectTargetName = lib$(ProjectName).dylib
endif
ifeq ($(OSNAME),Windows)
	ProjectTargetName = $(ProjectName).dll
endif
endif

ProjectTargetFile = $(addprefix $(BinaryDirectory),$(ProjectTargetName))

ProjectObjectFiles += $(addprefix $(ObjectDirectory),$(ObjectFiles)) $(patsubst %.cpp,%.o,$(QtMOCedFiles))
ProjectSourceFiles = $(addprefix $(SourceDirectory),$(SourceFiles))
ProjectHeaderFiles = $(addprefix $(HeaderDirectory),$(HeaderFiles))

# TODO: Add flags for IPO for release config

#all:
#	@echo OS=$(OSNAME)
#	@echo ProjectTargetName=$(ProjectTargetName)
#	@echo ProjectTargetFile=$(ProjectTargetFile)

#
# Compiler Options
#

ArchFlags :=
ifeq ($(Architecture),64)
ArchFlags += -m64
endif

ifeq ($(Architecture),32)
ArchFlags += -m32
endif

CommonCompilerFlags += -c -Wall -Wno-unused-function -fvisibility=hidden -funsigned-char -fno-strict-aliasing -I$(RootDirectory)Include $(ArchFlags)

ifeq ($(Configuration),Debug)
	CommonCompilerFlags += -g -DDX_DEBUG=1 -O0
else
	CommonCompilerFlags += -O2
endif
ifeq ($(ProjectType),SharedLibrary)
	CommonCompilerFlags += -fPIC
endif
ifeq ($(ProjectType),StaticLibrary)
	CommonCompilerFlags += -fPIC
endif

ifeq ($(CC),icc)
CommonCompilerFlags += -static-intel
LFLAGS += -static-intel
endif

CFLAGS += -x c $(CommonCompilerFlags) -Wno-pointer-sign
CXXFLAGS += -x c++  $(CommonCompilerFlags)

#
# Linker Options
#

LFLAGS += $(ArchFlags)
ifeq ($(Configuration),Debug)
	LFLAGS += -g -DDX_DEBUG
else
	LFLAGS += -O2
endif

ifeq ($(ProjectType),SharedLibrary)
ifeq ($(OSNAME),OSX)
	LFLAGS += -dynamiclib
else
	LFLAGS += -shared -Wl,-soname,$(ProjectTargetName)
endif
	LFLAGS += -fvisibility=hidden
endif

#
# Common Targets
#

.PHONY: Build Clean Force

Build: $(ProjectTargetFile) $(AfterBuild)

Clean: Force $(AfterClean)
	@-rm -rf $(ObjectDirectory)
	@-rm -f $(ProjectTargetFile)

ifeq ($(ProjectType),StaticLibrary)
$(ProjectTargetFile): $(ProjectObjectFiles) | $(BinaryDirectory)
	$(AR) -cq $@ $(ProjectObjectFiles)
else
$(ProjectTargetFile): $(ProjectObjectFiles) | $(BinaryDirectory)
	$(CC) $(LFLAGS) -o $@ $(ProjectObjectFiles) $(LIBS)
endif

$(ObjectDirectory)%.o : $(SourceDirectory)%.c $(ProjectHeaderFiles) | $(ObjectDirectory)
	$(CC) $(CFLAGS) -o $@ $<

$(ObjectDirectory)%.o : $(SourceDirectory)%.cpp $(ProjectHeaderFiles) | $(ObjectDirectory)
	$(CC) $(CXXFLAGS) -o $@ $<

$(ObjectDirectory)%.o : $(SourceDirectory)%.cxx $(ProjectHeaderFiles) | $(ObjectDirectory)
	$(CC) $(CXXFLAGS) -o $@ $<

$(BinaryDirectory):
	@mkdir -p $(BinaryDirectory)

$(ObjectDirectory):
	@mkdir -p $(ObjectDirectory)

Force:
