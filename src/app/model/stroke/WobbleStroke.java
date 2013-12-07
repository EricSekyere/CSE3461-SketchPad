package app.model.stroke;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class WobbleStroke implements Stroke {
	private Random random;
	private float detail = 2;
	private float amplitude = 2;
	private static final float FLATNESS = 1;

	public WobbleStroke( long seed, float detail, float amplitude ) {
		this.detail	= detail;
		this.amplitude	= amplitude;
		this.random = new Random( seed );
	}
	
	public WobbleStroke( float detail, float amplitude ) {
		this( System.currentTimeMillis(), detail, amplitude );
	}

	public Shape createStrokedShape( Shape shape ) {
		GeneralPath result = new GeneralPath();
		shape = new BasicStroke( 10 ).createStrokedShape( shape );
		PathIterator it = new FlatteningPathIterator( shape.getPathIterator( null ), FLATNESS );
		float points[] = new float[6];
		float moveX = 0, moveY = 0;
		float lastX = 0, lastY = 0;
		float thisX = 0, thisY = 0;
		int type = 0;
		float next = 0;

		while ( !it.isDone() ) {
			type = it.currentSegment( points );
			switch( type ){
			case PathIterator.SEG_MOVETO:
				moveX = lastX = randomize( points[0] );
				moveY = lastY = randomize( points[1] );
				result.moveTo( moveX, moveY );
				next = 0;
				break;

			case PathIterator.SEG_CLOSE:
				points[0] = moveX;
				points[1] = moveY;
				// Fall into....

			case PathIterator.SEG_LINETO:
				thisX = randomize( points[0] );
				thisY = randomize( points[1] );
				float dx = thisX-lastX;
				float dy = thisY-lastY;
				float distance = (float)Math.sqrt( dx*dx + dy*dy );
				if ( distance >= next ) {
					float r = 1.0f/distance;
					while ( distance >= next ) {
						float x = lastX + next*dx*r;
						float y = lastY + next*dy*r;
						result.lineTo( randomize( x ), randomize( y ) );
						next += detail;
					}
				}
				next -= distance;
				lastX = thisX;
				lastY = thisY;
				break;
			}
			it.next();
		}

		return result;
	}

    private float randomize( float x ) {
        return x+(float)random.nextDouble()*amplitude*2-1;
    }

}
