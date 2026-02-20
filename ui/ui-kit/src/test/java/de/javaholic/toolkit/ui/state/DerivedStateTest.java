package de.javaholic.toolkit.ui.state;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class DerivedStateTest {

    @Test
    void recomputesWhenDependencyChanges() {
        MutableState<Boolean> a = MutableState.of(false);
        MutableState<Boolean> b = MutableState.of(false);
        DerivedState<Boolean> both = DerivedState.of(() -> a.get() && b.get(), a, b);

        AtomicBoolean last = new AtomicBoolean(false);
        both.subscribe(last::set);

        a.set(true);
        assertThat(last.get()).isFalse();

        b.set(true);
        assertThat(last.get()).isTrue();
    }
}
