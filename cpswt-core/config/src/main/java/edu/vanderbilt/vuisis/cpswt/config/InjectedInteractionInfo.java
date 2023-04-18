package edu.vanderbilt.vuisis.cpswt.config;

import java.util.Map;

import static edu.vanderbilt.vuisis.cpswt.hla.InteractionRootInterface.ClassAndPropertyName;

public class InjectedInteractionInfo {

    public double InjectionTime;

    public String Interaction;

    public Map<ClassAndPropertyName, Object> ParameterValues;
}
