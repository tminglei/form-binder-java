package com.github.tminglei.bind;

import org.testng.annotations.BeforeClass;

import static com.github.tminglei.bind.Simple.*;
import static com.github.tminglei.bind.Mappings.*;
import static com.github.tminglei.bind.Constraints.*;
import static com.github.tminglei.bind.Processors.*;
import static com.github.tminglei.bind.FrameworkUtils.*;
import static com.github.tminglei.bind.Utils.*;

public class FormBinderTest {

    @BeforeClass
    public void start() {
        System.out.println(cyan("test form binder facade"));
    }
}
