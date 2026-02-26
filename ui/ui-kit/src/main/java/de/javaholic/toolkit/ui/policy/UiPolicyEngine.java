package de.javaholic.toolkit.ui.policy;

import de.javaholic.toolkit.ui.meta.UiProperty;

/**
 * Translates {@link UiProperty} metadata into a runtime policy decision.
 */
public interface UiPolicyEngine {

    UiDecision evaluate(UiProperty<?> property, UiPolicyContext context);
}
