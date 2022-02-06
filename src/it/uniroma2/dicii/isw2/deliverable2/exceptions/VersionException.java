package it.uniroma2.dicii.isw2.deliverable2.exceptions;

/**
 * A custom exception, to be thrown if problems with version arises,
 * such as "version not found" et similia.
 */
public class VersionException extends Exception {
    public VersionException(String string) {
        super(string);
    }
}
