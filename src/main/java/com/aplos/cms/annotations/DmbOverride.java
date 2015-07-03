package com.aplos.cms.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;

@Target({TYPE})
@Retention(RUNTIME)
public @interface DmbOverride {
	public Class<? extends DeveloperModuleBacking> dmbClass();
}
