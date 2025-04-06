package com.domsdevnest.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {

    @Test
    void unchecked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void unchecked_throw() {
        Service service = new Service();
        Assertions.assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(MyUncheckedException.class);
    }


    static class MyUncheckedException extends RuntimeException {
        public MyUncheckedException(String message) {
            super(message);
        }
    }

    /**
     * 언체크 예외는 예외를 잡지 않으면 자동으로 던지도록 되어 있음
     */

    static class Service {
        Repository repository = new Repository();
        /**
         * 예외를 잡아서 처리
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (UncheckedTest.MyUncheckedException e) {
                //예외 처리 로직
                log.info("예외 처리, message={}",e.getMessage(),e);
            }
        }

        /**
         * 예외를 밖으로 던지는 코드
         * 밖으로 던지려면 메소드에 throws 필수가 아님
         */
        public void callThrow() {
            repository.call();
        }
    }

    static class Repository {
        public void call() {
            throw new MyUncheckedException("ex");
        }
    }
}
