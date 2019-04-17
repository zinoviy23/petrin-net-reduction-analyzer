package hse.se.aaizmaylov.petrinetslibrary.petrinets.basic;

import hse.se.aaizmaylov.petrinetslibrary.petrinets.Edge;
import hse.se.aaizmaylov.petrinetslibrary.petrinets.PetriNetVertex;

public interface Place extends PetriNetVertex<
        Integer,
        Place,
        Transition,
        Edge<Integer, Transition, Place>,
        Edge<Integer, Place, Transition>> {
    int getMarks();

    void setMarks(int marks);

    int addMarks(int marks);

    int removeMarks(int marks);

    static Place withMarks(int marks, String label) {
        return new PlaceImpl(marks, label);
    }
}
