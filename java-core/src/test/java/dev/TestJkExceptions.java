package dev;

import org.junit.Test;
import xxx.joker.libs.core.exception.JkRuntimeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static xxx.joker.libs.core.util.JkConsole.display;

public class TestJkExceptions {

    @Test
    public void testException() {

        try {
//            a();
            m();
        } catch(JkRuntimeException ex) {
//            display("Exception toString()");
//            display("{}", ex);
//            display("\n-----------------------\n");
//            display("Exception getMessage()");
//            display("{}", ex.getMessage());
//            display("\n-----------------------\n");
//            display("Exception stack trace on System.out");
//            ex.printStackTrace(System.out);
//            display("\n-----------------------\n");
//            display("Exception stack trace on System.err");
//            ex.printStackTrace(System.err);

//            StringBuilder sb = new StringBuilder();
//            sb.append("Exception toString()\n");
//            sb.append(strLog("{}\n", ex));
//            sb.append("\n-----------------------\n\n");
//            sb.append("Exception getMessage()\n");
//            sb.append(strLog("{}\n", ex.getMessage()));
//            display("{}", sb.toString());
//
//            ex.printStackTrace(System.out);

                display("{}", ex);

//            display("\n-----------------------\n");
//            display("Exception stack trace on System.out");
//            ex.printStackTrace(System.out);
//            display("\n-----------------------\n");
//            display("Exception stack trace on System.err");
//            ex.printStackTrace(System.err);

        }


    }

    private void a() {
        b();
    }
    private void b() {
        c();
    }
    private void c() {
        throw new JkRuntimeException("Simple case of {}", "JkEcxeption");
    }

    private void m() {
        n();
    }
    private void n() {
        o();
    }
    private void o() {
        try {
            Files.readAllLines(Paths.get("sfsrgr"));
        } catch (IOException e) {
            throw new JkRuntimeException(e, "Exception wrapper case of {}", "JkEcxeption");
        }
    }
}
