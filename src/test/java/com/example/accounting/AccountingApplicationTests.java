package com.example.accounting;

import com.example.accounting.service.AccountingService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
class AccountingApplicationTests {

    private final String ACC_NUMBER = "1";
    @Autowired
    AccountingService accountingService;

    @Test
    void concurrentBalanceUpdatesTest() {
        accountingService.createNewAccount(ACC_NUMBER);
        updateAccountConcurrently(1000, ACC_NUMBER);
        assertEquals(0L, accountingService.getAccountDetails(ACC_NUMBER).getBalance());
    }

    @SneakyThrows
    private void updateAccountConcurrently(int numberOfThreads, String accNumber) {
        CountDownLatch start = new CountDownLatch(numberOfThreads);
        CountDownLatch finish = new CountDownLatch(numberOfThreads * 2);
        for (long i = 1; i <= numberOfThreads; i++) {
            new AccountUpdater(i, start, finish).run();
            new AccountUpdater(-i, start, finish).run();
            start.countDown();
        }
        finish.await();
    }

    @Data
    @AllArgsConstructor
    class AccountUpdater {
        private Long amount;
        private CountDownLatch start;
        private CountDownLatch finish;

        public void run() {
            new Thread(new Runnable() {
                @Override
                @SneakyThrows
                public void run() {
                    start.await();
                    log.info("Updated by " + amount);
                    accountingService.updateAccount(ACC_NUMBER, amount, true);
                    finish.countDown();
                }
            }).start();
        }
    }
}
