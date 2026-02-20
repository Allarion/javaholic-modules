package de.javaholic.toolkit.ui.state;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class MutableStateTest {

    @Test
    void notifiesSubscriberAndCanUnsubscribe() {
        MutableState<Integer> state = MutableState.of(1);
        AtomicInteger observed = new AtomicInteger(0);

        Subscription subscription = state.subscribe(observed::set);
        state.set(2);

        assertThat(observed.get()).isEqualTo(2);

        subscription.unsubscribe();
        state.set(3);

        assertThat(observed.get()).isEqualTo(2);
    }
}
