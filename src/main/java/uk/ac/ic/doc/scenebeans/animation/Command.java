package uk.ac.ic.doc.scenebeans.animation;

public interface Command {
    void invoke() throws CommandException;
}
