cmake_minimum_required(VERSION 3.10.2)
add_library(
        native-lib
        SHARED
        native-lib.cpp
)
target_link_libraries(
        native-lib
        ${log-lib}
)