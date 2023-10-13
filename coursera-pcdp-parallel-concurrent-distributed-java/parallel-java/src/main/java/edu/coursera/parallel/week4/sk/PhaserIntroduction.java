package edu.coursera.parallel.week4.sk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Phaser;

public class PhaserIntroduction {
    private static final Logger log = LoggerFactory.getLogger(PhaserIntroduction.class);

    public static void main(String[] args) {
        final var phaser = new Phaser(3) {
            @Override
            protected boolean onAdvance(int phase, int registeredParties) {
                log("inside onAdvance", this);
                return true;
            }
        };
        log("after constructor", phaser);

        phaser.register();
        log("after register()", phaser);

        phaser.arrive();
        log("after arrive()", phaser);

        final var thread = new Thread(() -> {
            log("before arriveAndAwaitAdvance()", phaser);
            phaser.arriveAndAwaitAdvance();
            log("after arriveAndAwaitAdvance()", phaser);
        });
        thread.start();

        phaser.arrive();
        log("after arrive()", phaser);

        phaser.arriveAndDeregister();
        log("after arriveAndDeregister()", phaser);
    }

    private static void log(String message, Phaser phaser) {
        log.info("{} phase: {}, registered/arrived/unarrived: {}={}+{}, terminated: {}",
                String.format("%-40s", message),
                phaser.getPhase(),
                phaser.getRegisteredParties(),
                phaser.getArrivedParties(),
                phaser.getUnarrivedParties(),
                phaser.isTerminated());
    }
}
