ProjectNameShort = ddfp
VersionSuffix ?= dev
ProjectName ?= $(ProjectNameShort)$(VersionSuffix)

Configuration ?= debug
Architecture ?= 64

OsName :=
ifeq ($(OS),Windows_NT)
	OsName := Windows
else
	UNAME_S := $(shell uname -s)
ifeq ($(UNAME_S),Linux)
	OsName := Linux
endif
ifeq ($(UNAME_S),Darwin)
	OsName := OSX
endif
endif

RootDirectory = .
BinaryDirectory ?= $(RootDirectory)/bin/$(Configuration)/$(OsName)/$(Architecture)
ObjectDirectory ?= $(RootDirectory)/obj/$(Configuration)/$(OsName)/$(Architecture)
SourceDirectory ?= $(RootDirectory)
HeaderDirectory ?= $(RootDirectory)

ifeq ($(OsName),Windows)
	ProjectTargetName = $(ProjectName).dll
endif
ifeq ($(OsName),Linux)
	ProjectTargetName = lib$(ProjectName).so
endif
ifeq ($(OsName),OSX)
	ProjectTargetName = lib$(ProjectName).dylib
endif
ProjectTargetFile = $(addprefix $(BinaryDirectory)/,$(ProjectTargetName))

CC = gcc  # C compiler
CFLAGS += -c -Wall -Wno-unused-function -fvisibility=hidden -funsigned-char -fno-strict-aliasing -fPIC -DAPI_PREFIX=${ProjectName}_
LFLAGS += -shared # -nostdlib -lmingw32 -lgcc_s -lgcc -lmoldname -lmingwex -lmsvcrt -lpthread -ladvapi32 -lshell32 -luser32 -lkernel32 -liconv -lmingw32 -lgcc_s -lgcc -lmoldname -lmingwex -lmsvcrt --verbose # --verbose # linking flags

LFLAGS += -L$(RootDirectory)/bin/$(Configuration)/$(OsName)/$(Architecture)
ifeq ($(Configuration),debug)
	CFLAGS += -g -DDX_DEBUG=1 -O0
	LFLAGS += -g -DDX_DEBUG
else
	CFLAGS += -O2
	LFLAGS += -O2
endif
LFLAGS += -fvisibility=hidden -shared -Wl,-soname,$(ProjectTargetName)

ifeq ($(Architecture),32)
CFLAGS += -m32
LFLAGS += -m32
endif
ifeq ($(Architecture),64)
CFLAGS += -m64
LFLAGS += -m64
endif

SourceFiles = $(wildcard $(SourceDirectory)/*.c)
ObjectFiles = $(patsubst %.cxx,%.o,$(patsubst %.c,%.o,$(patsubst %.cpp,%.o,$(SourceFiles))))
ProjectObjectFiles += $(addprefix $(ObjectDirectory)/,$(ObjectFiles))


#
# Common Targets
#

.PHONY: build

rebuild: clean build

build: $(ProjectTargetFile) $(AfterBuild)

clean:
ifneq (sh, $(findstring sh, $(SHELL)))
	@-rmdir /s /q "$(ObjectDirectory)"
	@-rmdir /s /q "$(BinaryDirectory)"
else
	@-rm -rf $(ObjectDirectory)
	@-rm -rf $(BinaryDirectory)
endif

$(ProjectTargetFile): $(ProjectObjectFiles) | $(BinaryDirectory)
	$(CC) $(LFLAGS) -o $@ $(ProjectObjectFiles) $(LIBS)

$(ObjectDirectory)/%.o : $(SourceDirectory)/%.c | $(ObjectDirectory)
	$(CC) $(CFLAGS) -o $@ $<

$(BinaryDirectory):
ifneq (sh, $(findstring sh, $(SHELL)))
	@mkdir "$(BinaryDirectory)"
else
	@mkdir -p $(BinaryDirectory)
endif

$(ObjectDirectory):
ifneq (sh, $(findstring sh, $(SHELL)))
	@mkdir "$(ObjectDirectory)"
else
	@mkdir -p $(ObjectDirectory)
endif
