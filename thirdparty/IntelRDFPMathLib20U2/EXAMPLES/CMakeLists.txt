add_executable (demo demo.c)
target_link_libraries (demo LINK_PUBLIC bid)
install(TARGETS demo DESTINATION ./binDemo/${CMAKE_BUILD_TYPE}/${INSTALL_SUFFIX})
