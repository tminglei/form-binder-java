package com.github.tminglei.bind;

import static com.github.tminglei.bind.Simple.*;
import static com.github.tminglei.bind.Mappings.*;

public class FormBinderTest {
    public void ttt() {
        mapping(
            fb("t").to(vInt())
        ).verifying((label, vObj, messages) -> {
            return null;
        });
    }
}
