package hse.se.aaizmaylov.petrinetslibrary.petrinets.basic.analysis;

import hse.se.aaizmaylov.petrinetslibrary.petrinets.Edge;
import hse.se.aaizmaylov.petrinetslibrary.petrinets.analysis.DeleteVertexCallback;
import hse.se.aaizmaylov.petrinetslibrary.petrinets.analysis.Reduction;
import hse.se.aaizmaylov.petrinetslibrary.petrinets.PetriNet;
import hse.se.aaizmaylov.petrinetslibrary.petrinets.basic.Place;
import hse.se.aaizmaylov.petrinetslibrary.petrinets.basic.Transition;
import lombok.NonNull;
import org.apache.log4j.Logger;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class Reducer {
    private final static Logger LOGGER = Logger.getLogger(Reducer.class);

    private PetriNet<Place, Transition> petriNet;

    private boolean reduced = false;

    private List<Place> placesToDelete = new ArrayList<>();
    private List<Transition> transitionsToDelete = new ArrayList<>();

    private Set<Place> visitedPlace = new HashSet<>();
    private Set<Transition> visitedTransition = new HashSet<>();

    private Collection<? extends Reduction<Place, Transition>> reductionsOnPlaces;
    private Collection<? extends Reduction<Transition, Place>> reductionsOnTransitions;

    private DeleteVertexCallback<Place, Transition> deletePlaceCallback = new DeleteVertexCallback<Place, Transition>() {
        @Override
        public void onDeleteTarget(Place place) {
            LOGGER.debug("Delete " + place);
            placesToDelete.add(place);
        }

        @Override
        public void onDeleteNeighbour(Transition transition) {
            LOGGER.debug("Delete " + transition);
            transitionsToDelete.add(transition);
        }
    };

    private DeleteVertexCallback<Transition, Place> deleteTransitionCallback =
            DeleteVertexCallback.invertedAdapter(deletePlaceCallback);

    public Reducer(@NonNull PetriNet<Place, Transition> petriNet) {
        this.petriNet = petriNet;
    }

    public void reduce(@NonNull Collection<? extends Reduction<Place, Transition>> reductionsOnPlaces,
                       @NonNull Collection<? extends Reduction<Transition, Place>> reductionsOnTransitions) {
        LOGGER.info("Reductions started");

        if (reduced) {
            throw new IllegalStateException("Petri Net already reduced");
        }

        this.reductionsOnPlaces = reductionsOnPlaces;
        this.reductionsOnTransitions = reductionsOnTransitions;

        boolean prevReduced = true;

        while (prevReduced) {
            boolean reducedSmth = false;

            for (Place place : petriNet.getPlaces()) {
                if (!visitedPlace.contains(place)) {
                    reducedSmth = reducedSmth || placeDFS(place);
                }
            }

            for (Transition transition : petriNet.getTransitions()) {
                if (!visitedTransition.contains(transition)) {
                    reducedSmth = reducedSmth || transitionDFS(transition);
                }
            }

            visitedTransition.clear();
            visitedPlace.clear();

            prevReduced = reducedSmth;

            deleteVertices();
        }
//тьмок тьмок

//тьмок
        reduced = true;
    }

    private boolean placeDFS(Place current) {
        visitedPlace.add(current);

        boolean reducedSmth = false;
        for (Reduction<Place, Transition> reduction : reductionsOnPlaces) {
            reducedSmth = reducedSmth || reduction.reduceFrom(current, deletePlaceCallback);
        }

        return reducedSmth || current.getOutputs().stream()
                .map(Edge::getToEndpoint)
                .filter(transition -> !visitedTransition.contains(transition))
                .collect(toList()).stream()
                .anyMatch(this::transitionDFS);
    }

    private boolean transitionDFS(Transition current) {
        visitedTransition.add(current);

        boolean reducedSmth = false;
        for (Reduction<Transition, Place> reduction : reductionsOnTransitions) {
            reducedSmth = reducedSmth || reduction.reduceFrom(current, deleteTransitionCallback);
        }

        return reducedSmth || current.getOutputs().stream()
                .map(Edge::getToEndpoint)
                .filter(place -> !visitedPlace.contains(place))
                .collect(toList()).stream()
                .anyMatch(this::placeDFS);
    }

    private void deleteVertices() {
        for (Place place : placesToDelete) {
            petriNet.removePlace(place);
        }

        for (Transition transition : transitionsToDelete) {
            petriNet.removeTransition(transition);
        }
    }
}
