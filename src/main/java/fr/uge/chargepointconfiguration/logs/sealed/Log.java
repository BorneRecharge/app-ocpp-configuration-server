package fr.uge.chargepointconfiguration.logs.sealed;

import fr.uge.chargepointconfiguration.logs.CustomLogger;

/**
 * Interface used by {@link CustomLogger}.
 */
public sealed interface Log permits BusinessLog, TechnicalLog {}
