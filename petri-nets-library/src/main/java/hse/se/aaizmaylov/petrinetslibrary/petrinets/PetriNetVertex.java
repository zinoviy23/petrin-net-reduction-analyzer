package hse.se.aaizmaylov.petrinetslibrary.petrinets;

import java.util.Set;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public interface PetriNetVertex<
        TTokenContainer,
        TSelf,
        TNeighbours,
        TInput extends Edge<TTokenContainer, TNeighbours, TSelf>,
        TOutput extends Edge<TTokenContainer, TSelf, TNeighbours>>
        extends LabeledVertex {

    Set<TInput> getInputs();

    Set<TOutput> getOutputs();

    boolean removeOutput(TOutput output);

    boolean addOutput(TOutput output);

    boolean removeInput(TInput input);

    boolean addInput(TInput input);
}
