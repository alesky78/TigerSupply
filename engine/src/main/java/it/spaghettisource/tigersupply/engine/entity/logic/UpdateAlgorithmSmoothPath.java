package it.spaghettisource.tigersupply.engine.entity.logic;

import static it.spaghettisource.tigersupply.engine.utils.StaticResources.ALGPRO_LIST_POINTS;
import it.spaghettisource.tigersupply.engine.entity.Position;
import it.spaghettisource.tigersupply.engine.entity.Speed;
import it.spaghettisource.tigersupply.engine.utils.DynaProperties;

import java.awt.Point;
import java.util.List;

/**
 * {@link UpdateAlgorithm} that drives the entity along a smooth curve passing through a set of ordered
 * control points, evaluated on the fly with a uniform Catmull-Rom spline.
 *
 * <p>Unlike {@link UpdateAlgorithmLinearPath}, whose segments stay straight, this algorithm rounds the
 * control points into a smooth curve; unlike the old snap-a-point-per-frame behaviour, it moves at a
 * constant, frame-rate-independent pace: {@link #init(DynaProperties)} reads the control points supplied
 * under the {@code ALGPRO_LIST_POINTS} key (a {@link List} of {@link Point}), and every frame
 * {@link #updateLogic(Position, Speed, float)} advances the entity along the curve by a distance derived
 * from the magnitude of the reference {@link Speed} integrated over the elapsed time. The first and last
 * control points are the start and end of the path; once the end is reached the entity stops moving.</p>
 *
 * @author Alessandro D'Ottavio
 *
 */
public class UpdateAlgorithmSmoothPath extends AbstractUpdateAlgorithm {

	/** Parametric probe used to estimate the local tangent length {@code |dP/du|}. */
	private static final float EPS = 1e-3f;

	/** Control point coordinates; the curve starts at index 0 and ends at the last index. */
	private float[] px;
	private float[] py;

	/** Index of the current segment (from control point {@code seg} to {@code seg + 1}). */
	private int seg;

	/** Local parameter within the current segment, in {@code [0, 1)}. */
	private float u;

	/** Whether the end of the path has been reached. */
	private boolean finished;

	/** Magnitude of the reference speed, in pixel/second, derived on the first frame. */
	private float referenceSpeed;

	/** Whether {@link #referenceSpeed} still has to be derived from the entity speed. */
	private boolean computeReferenceSpeed = true;

	/**
	 * {@inheritDoc}
	 *
	 * <p>On the first invocation it derives the constant travel speed from the magnitude of the
	 * reference {@link Speed}. Each frame it advances the entity along the Catmull-Rom curve by that
	 * speed integrated over the elapsed time, walking across as many segments as the budget covers;
	 * once the last control point is reached the entity stops.</p>
	 */
	public void updateLogic(Position position, Speed speed, float deltaSeconds) {

		if(computeReferenceSpeed){
			referenceSpeed = (float) Math.sqrt(speed.getSpeedX()*speed.getSpeedX() + speed.getSpeedY()*speed.getSpeedY());
			computeReferenceSpeed = false;
		}

		if(finished){
			return;	//path exhausted, the entity stops moving
		}

		int lastSegment = px.length - 2;
		float budget = referenceSpeed * deltaSeconds;

		while(budget > 0 && !finished){

			float hereX = evalX(seg, u);
			float hereY = evalY(seg, u);

			//estimate the local speed |dP/du| by a small forward probe, so the world step stays constant
			float h = Math.min(u + EPS, 1f) - u;
			float tangent = 1e-4f;
			if(h > 0){
				float dx = evalX(seg, u + h) - hereX;
				float dy = evalY(seg, u + h) - hereY;
				tangent = Math.max((float) Math.sqrt(dx*dx + dy*dy) / h, 1e-4f);
			}

			float du = budget / tangent;

			if(u + du < 1f){
				//advance inside the current segment
				u += du;
				position.setPosX(evalX(seg, u));
				position.setPosY(evalY(seg, u));
				budget = 0;
			}else{
				//consume the rest of this segment and carry the remaining budget to the next one
				float endX = evalX(seg, 1f);
				float endY = evalY(seg, 1f);
				float dx = endX - hereX;
				float dy = endY - hereY;
				budget -= (float) Math.sqrt(dx*dx + dy*dy);
				seg++;
				u = 0f;
				if(seg > lastSegment){
					position.setPosX(endX);
					position.setPosY(endY);
					finished = true;
				}else{
					position.setPosX(evalX(seg, 0f));
					position.setPosY(evalY(seg, 0f));
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Reads the control points from the {@code ALGPRO_LIST_POINTS} property and positions the cursor
	 * at the first one. A path with fewer than two control points cannot form a curve, so the entity is
	 * left stationary.</p>
	 */
	@SuppressWarnings("unchecked")
	public void init(DynaProperties properties){
		List<Point> configPoint = (List<Point>) properties.getList(ALGPRO_LIST_POINTS);

		int n = configPoint.size();
		px = new float[n];
		py = new float[n];
		for (int i = 0; i < n; i++) {
			Point point = configPoint.get(i);
			px[i] = point.x;
			py[i] = point.y;
		}

		seg = 0;
		u = 0f;
		finished = n < 2;	//a curve needs at least two control points
	}

	/**
	 * Evaluates the x coordinate of the uniform Catmull-Rom curve on segment {@code segment} at local
	 * parameter {@code t}. The neighbouring control points are clamped at the ends so the curve passes
	 * through the first and last control point.
	 *
	 * @param segment the segment index (from control point {@code segment} to {@code segment + 1})
	 * @param t the local parameter in {@code [0, 1]}
	 * @return the interpolated x coordinate
	 */
	private float evalX(int segment, float t){
		return catmullRom(coord(px, segment - 1), coord(px, segment), coord(px, segment + 1), coord(px, segment + 2), t);
	}

	/**
	 * Evaluates the y coordinate of the uniform Catmull-Rom curve on segment {@code segment} at local
	 * parameter {@code t}.
	 *
	 * @param segment the segment index (from control point {@code segment} to {@code segment + 1})
	 * @param t the local parameter in {@code [0, 1]}
	 * @return the interpolated y coordinate
	 */
	private float evalY(int segment, float t){
		return catmullRom(coord(py, segment - 1), coord(py, segment), coord(py, segment + 1), coord(py, segment + 2), t);
	}

	/**
	 * Returns the coordinate at {@code index}, clamping to the first/last control point so the ends of
	 * the path are reached exactly.
	 *
	 * @param values the coordinate array
	 * @param index the (possibly out-of-range) control point index
	 * @return the clamped coordinate
	 */
	private float coord(float[] values, int index){
		if(index < 0){
			return values[0];
		}
		if(index > values.length - 1){
			return values[values.length - 1];
		}
		return values[index];
	}

	/**
	 * Uniform Catmull-Rom interpolation of a single coordinate between {@code p1} and {@code p2}.
	 *
	 * @param p0 the control point before the segment
	 * @param p1 the segment start
	 * @param p2 the segment end
	 * @param p3 the control point after the segment
	 * @param t the local parameter in {@code [0, 1]}
	 * @return the interpolated coordinate
	 */
	private float catmullRom(float p0, float p1, float p2, float p3, float t){
		return 0.5f * ( (2f * p1)
				+ (-p0 + p2) * t
				+ (2f*p0 - 5f*p1 + 4f*p2 - p3) * t * t
				+ (-p0 + 3f*p1 - 3f*p2 + p3) * t * t * t );
	}

}
