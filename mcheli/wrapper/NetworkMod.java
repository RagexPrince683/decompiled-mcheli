package mcheli.wrapper;

import java.lang.annotation.Annotation;

public @interface NetworkMod
{
  boolean clientSideRequired();
  
  boolean serverSideRequired();
}
