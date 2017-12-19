package simulator.LocationAlgorithms.EvaluationShapes;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import view.Position;
import view.SimDrawable;

/**
 * Created by נועם on 12/14/2017.
 */
public abstract class EvaluationShape implements SimDrawable {
    @Override
    public Position getPosition() {
        return null;
    }

    @Override
    public void draw(PaintEvent e, int x, int y, int r, int x0, int y0) {

    }
}
