package com.es.shell;

import java.util.List;

public interface Shell {

    ExecuteResult execute(ExecuteRequest request);
    List<ExecuteResult> executeBatch(ExecuteRequest request);

}
