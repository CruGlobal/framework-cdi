package org.ccci.framework.cdi.services;

import java.io.File;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

public interface PathHelper
{

    @Produces
    @Named("appRootAsAbsoluteFile")
    public abstract File getAppRootAsAbsoluteFile();

    public abstract String getRealPath(String path);

}
