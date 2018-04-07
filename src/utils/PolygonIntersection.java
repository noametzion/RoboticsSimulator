package utils;


import view.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.*;

public class PolygonIntersection {
    private boolean isEqual(double d1, double d2)
    {
       return Math.abs(d1-d2)< 0.00000001;
    }

    //lines intersection
    public  Position getIntersectionPoint(Position l1p1, Position l1p2, Position l2p1, Position l2p2)
    {
        double A1 = l1p2.y - l1p1.y;
        double B1 = l1p1.x - l1p2.x;
        double C1 = A1 * l1p1.x + B1 * l1p1.y;

        double A2 = l2p2.y - l2p1.y;
        double B2 = l2p1.x - l2p2.x;
        double C2 = A2 * l2p1.x + B2 * l2p1.y;

        //lines are parallel
        double det = A1 * B2 - A2 * B1;
        if (isEqual(det, 0d))
        {
            return null; //parallel lines
        }
        else
        {
            double x = (B2 * C1 - B1 * C2) / det;
            double y = (A1 * C2 - A2 * C1) / det;
            boolean online1 = ((Math.min(l1p1.x, l1p2.x) < x || isEqual(Math.min(l1p1.x, l1p2.x), x))
                    && (Math.max(l1p1.x, l1p2.x) > x || isEqual(Math.max(l1p1.x, l1p2.x), x))
                    && (Math.min(l1p1.y, l1p2.y) < y || isEqual(Math.min(l1p1.y, l1p2.y), y))
                    && (Math.max(l1p1.y, l1p2.y) > y || isEqual(Math.max(l1p1.y, l1p2.y), y))
            );
            boolean online2 = ((Math.min(l2p1.x, l2p2.x) < x || isEqual(Math.min(l2p1.x, l2p2.x), x))
                    && (Math.max(l2p1.x, l2p2.x) > x || isEqual(Math.max(l2p1.x, l2p2.x), x))
                    && (Math.min(l2p1.y, l2p2.y) < y || isEqual(Math.min(l2p1.y, l2p2.y), y))
                    && (Math.max(l2p1.y, l2p2.y) > y || isEqual(Math.max(l2p1.y, l2p2.y), y))
            );

            if (online1 && online2)
                return new Position(x, y);
        }
        return null; //intersection is at out of at least one segment.
    }

    public boolean isPointInsidePoly(Position test, ConvexPolygon2D poly)
    {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = poly.corners.size() - 1; i < poly.corners.size(); j = i++)
        {
            if ((poly.corners.get(i).y > test.y) != (poly.corners.get(j).y > test.y) &&
                    (test.x < (poly.corners.get(j).x - poly.corners.get(i).x) * (test.y - poly.corners.get(i).y) / (poly.corners.get(j).y - poly.corners.get(i).y) + poly.corners.get(i).x))
            {
                result = !result;
            }
        }
        return result;
    }
    public  List<Position> getIntersectionPoints(Position l1p1, Position l1p2, ConvexPolygon2D poly)
    {
        List<Position> intersectionPoints = new ArrayList<>();
        for (int i = 0; i < poly.corners.size(); i++)
        {

            int next = (i + 1 == poly.corners.size()) ? 0 : i + 1;

            Position ip = getIntersectionPoint(l1p1, l1p2, poly.corners.get(i), poly.corners.get(next));

            if (ip != null) intersectionPoints.add(ip);

        }

        return intersectionPoints;
    }

    private void addPoints(List<Position> pool, List<Position> newpoints)
    {
        for (Position np :newpoints) {
            boolean found = false;
            for (Position p :pool)
            {
                if (isEqual(p.x, np.x) && isEqual(p.y, np.y))
                {
                    found = true;
                    break;
                }
            }
            if (!found) pool.add(np);
        }
    }


    public List<Position> orderClockwise(List<Position> points)
    {
        double mX = 0;
        double mY = 0;
        for (Position p : points)
        {
            mX += p.x;
            mY += p.y;
        }
        final double aveX= mX / points.size();
        final double aveY= mY / points.size();
        Collections.sort(points, new Comparator<Position>() {
            @Override
            public int compare(Position c1, Position c2) {
                return Double.compare(Math.atan2(c1.x - aveX, c1.y - aveY), Math.atan2(c2.x - aveX, c2.y - aveY));
            }
        });
        return points;

    }
    public ConvexPolygon2D getIntersectionOfPolygons(ConvexPolygon2D poly1, ConvexPolygon2D poly2)
    {
        List<Position> clippedCorners = new ArrayList<>();

        //Add  the corners of poly1 which are inside poly2
        for (int i = 0; i < poly1.corners.size(); i++)
        {
            if (isPointInsidePoly(poly1.corners.get(i), poly2)){
                ArrayList<Position> al= new ArrayList<>();
                al.add(new Position(poly1.corners.get(i).x,poly1.corners.get(i).y));
                addPoints(clippedCorners,al);
            }

        }

        //Add the corners of poly2 which are inside poly1
        for (int i = 0; i < poly2.corners.size(); i++)
        {
            if (isPointInsidePoly(poly2.corners.get(i),poly1)) {
                ArrayList<Position> al = new ArrayList<>();
                al.add(new Position(poly2.corners.get(i).x, poly2.corners.get(i).y));
                addPoints(clippedCorners, al);
            }
        }

        //Add  the intersection points
        if(poly1.corners.size()>1) {
            for (int i = 0, next = 1; i < poly1.corners.size(); i++, next = (i + 1 == poly1.corners.size()) ? 0 : i + 1) {

                addPoints(clippedCorners, getIntersectionPoints(poly1.corners.get(i), poly1.corners.get(next), poly2));
            }
        }

        return new ConvexPolygon2D(orderClockwise( clippedCorners));
    }

    public Position getCenterPositionOfPolygon(ConvexPolygon2D poly){
        double posX=0;
        double posY=0;
        for (Position p: poly.corners
             ) {
            posX+=p.x;
            posY+= p.y;
        }
        posX/=poly.corners.size();
        posY/=poly.corners.size();

        return new Position(posX,posY);
    }
}
