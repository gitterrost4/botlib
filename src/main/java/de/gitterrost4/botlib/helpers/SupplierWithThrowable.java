package de.gitterrost4.botlib.helpers;

@FunctionalInterface
public interface SupplierWithThrowable<O, T extends Throwable> {
  public O get() throws T;
}
