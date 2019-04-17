package hse.se.aaizmaylov.petrinetslibrary.petrinets.basic.reductions;

import hse.se.aaizmaylov.petrinetslibrary.petrinets.analysis.Reduction;
import hse.se.aaizmaylov.petrinetslibrary.petrinets.basic.*;
import org.junit.jupiter.api.Test;

import static hse.se.aaizmaylov.petrinetslibrary.utils.CollectionsUtils.first;
import static org.junit.jupiter.api.Assertions.*;

class FusionOfSelfLoopPlacesTest {
    @Test
    void checkReduction() {
        Place place = Place.withMarks(1, "p1");
        Transition transition = new TransitionImpl("t1");
        Place place1 = Place.withMarks(0, "p2");

        transition.addOutput(new FromTransitionToPlaceEdge(transition, place));
        transition.addInput(new FromPlaceToTransitionEdge(place, transition));
        transition.addOutput(new FromTransitionToPlaceEdge(transition, place1));

        FusionOfSelfLoopPlaces reduction = new FusionOfSelfLoopPlaces();

        assertTrue(reduction.reduceFrom(transition));
        assertTrue(transition.getInputs().isEmpty());
        assertEquals(1, transition.getOutputs().size());
        assertEquals(place1, first(transition.getOutputs()).getToEndpoint());

        assertTrue(place.getInputs().isEmpty() && place.getOutputs().isEmpty());
    }

    @Test
    void checkReductionDisabledBecauseOfMarks() {
        Place place = Place.withMarks(0, "p1");
        Transition transition = new TransitionImpl("t1");
        Place place1 = Place.withMarks(0, "p2");

        transition.addOutput(new FromTransitionToPlaceEdge(transition, place));
        transition.addInput(new FromPlaceToTransitionEdge(place, transition));
        transition.addOutput(new FromTransitionToPlaceEdge(transition, place1));

        FusionOfSelfLoopPlaces reduction = new FusionOfSelfLoopPlaces();

        assertFalse(reduction.reduceFrom(transition));
        assertEquals(1, transition.getInputs().size());
        assertEquals(2, transition.getOutputs().size());
    }

    @Test
    void checkReductionDisabledBecauseOfEdges() {
        Place place = Place.withMarks(1, "p1");
        Transition transition = new TransitionImpl("t1");
        Transition transition1 = new TransitionImpl("t2");
        Place place1 = Place.withMarks(0, "p2");

        transition.addOutput(new FromTransitionToPlaceEdge(transition, place));
        transition.addInput(new FromPlaceToTransitionEdge(place, transition));
        transition.addOutput(new FromTransitionToPlaceEdge(transition, place1));
        transition1.addOutput(new FromTransitionToPlaceEdge(transition1, place));

        FusionOfSelfLoopPlaces reduction = new FusionOfSelfLoopPlaces();

        assertFalse(reduction.reduceFrom(transition));
        assertEquals(1, transition.getInputs().size());
        assertEquals(2, transition.getOutputs().size());
        assertEquals(2, place.getInputs().size());
        assertEquals(1, place.getOutputs().size());
    }

    @Test
    void disabledBecauseNothingLoops() {
        Transition transition = new TransitionImpl("t1");
        Transition transition1 = new TransitionImpl("t2");
        Place place = Place.withMarks(1, "p1");

        place.addOutput(new FromPlaceToTransitionEdge(place, transition1));
        transition.addOutput(new FromTransitionToPlaceEdge(transition, place));

        Reduction<Transition> reduction = new FusionOfSelfLoopPlaces();
        assertFalse(reduction.reduceFrom(transition));
    }
}