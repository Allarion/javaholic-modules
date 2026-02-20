package de.javaholic.toolkit.ui.state;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

class TriggerTest {

    @Test
    void fireIncrementsValueAndNotifies() {
        Trigger trigger = new Trigger();
        AtomicLong observed = new AtomicLong(0);
        trigger.subscribe(observed::set);

        trigger.fire();
        trigger.fire();

        assertThat(trigger.get()).isEqualTo(2L);
        assertThat(observed.get()).isEqualTo(2L);
    }
}
