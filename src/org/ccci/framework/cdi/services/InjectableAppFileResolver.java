package org.ccci.framework.cdi.services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ccci.framework.util.IFileResolver;
import org.ccci.util.LoadFromClasspath;

/**
 * AppFileResolver.java
 * 
 * Resolves files for a web application.  This is primarily for resolving
 * configuration files and some serialized files.  It stores files within
 * the "WEB-INF" folder of the web application whenever possible.
 * 
 * Filenames passed to these methods should be bare (e.g. "menu.xml").
 * 
 * Original created on Nov 7, 2003
 * Injectable version created Feb 15, 2009
 * 
 * (C) Copyright 2003 Campus Crusade for Christ
 * 
 * @author Nathan Kopp
 */
@ApplicationScoped
public class InjectableAppFileResolver implements IFileResolver
{
    @Inject PathHelper pathHelper;
    
    public InjectableAppFileResolver()
    {
    }
    
    /**
     * 
     * @param file
     * @return String
     */
    public String getRealPath(String file)
    {
        if (file.charAt(0)!='/') file = "/"+file;
        return pathHelper.getRealPath("/WEB-INF"+file);
    }
    
    /**
     * Creates an input stream for a resource.
     * 
     * @param file
     * @param context
     * @return InputStream
     */
    public InputStream getInputStream(String file)
    {
        String prefix =  pathHelper.getRealPath("/WEB-INF");
        if (file.charAt(0)!='/') file = "/"+file;
        
        InputStream in = null;

        try { in = new FileInputStream(prefix+file); }
        catch(Exception e) { }
        
        // try the old way (from the classpath) just in case
        if (in==null)
        {
            in = LoadFromClasspath.getStream(file);
        }

        return in;
    }

    /**
     * Creates an output stream for a resource.
     * 
     * @param file
     * @param context
     * @return OutputStream
     */
    public OutputStream getOutputStream(String file)
    {
        String prefix =  pathHelper.getRealPath("/WEB-INF");
        if (file.charAt(0)!='/') file = "/"+file;
        
        OutputStream in = null;

        try { in = new FileOutputStream(prefix+file); }
        catch(Exception e) { }

        return in;
    }

}
