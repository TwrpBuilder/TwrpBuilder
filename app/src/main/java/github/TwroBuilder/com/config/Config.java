package github.TwroBuilder.com.config;

import java.io.File;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by sumit on 12/11/16.
 */

public class Config
{
   public static final boolean suAvailable = Shell.SU.available();
}
