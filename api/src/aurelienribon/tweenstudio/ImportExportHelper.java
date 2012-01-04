package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenstudio.TweenStudio.DummyTweenAccessor;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import java.util.Locale;
import java.util.Map;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
class ImportExportHelper {
	public static Timeline stringToTimeline(String str) {
		Timeline tl = Timeline.createParallel();
		String[] lines = str.split("\n");

		for (String line : lines) {
			String[] parts = line.split(";");
			if (parts.length < 6) continue;

			Object target = new DummyTweenAccessor(parts[0]); // name instead of real target
			int tweenType = Integer.parseInt(parts[1]);
			int delay = Integer.parseInt(parts[2]);
			int duration = Integer.parseInt(parts[3]);
			TweenEquation equation = TweenEquation.parse(parts[4]);

			float[] targets = new float[parts.length-5];
			for (int i=0; i<targets.length; i++) targets[i] = Float.parseFloat(parts[i+5]);

			Tween tween = Tween.to(target, tweenType, duration)
				.target(targets)
				.ease(equation)
				.delay(delay);
			
			tl.push(tween);
		}

		return tl;
	}

	public static void timelineToModel(Timeline timeline, TimelineModel model, Map<Object, String> targetsNamesMap, Editor editor) {
		for (BaseTween child : timeline.getChildren()) {
			Tween tween = (Tween) child;

			String targetName = targetsNamesMap.get(tween.getTarget());
			String propertyName = editor.getProperty(tween.getTarget().getClass(), tween.getType()).getName();

			Element elem = model.getElement(targetName + "/" + propertyName);
			if (elem != null) {
				NodeData nodeData = new NodeData(tween.getCombinedTweenCount());
				nodeData.setEquation(tween.getEasing());
				nodeData.setTargets(tween.getTargetValues());

				Node node = elem.addNode(tween.getFullDuration());
				node.setLinked(tween.getDuration() > 0 || child == timeline.getChildren().get(0));
				node.setUserData(nodeData);
			} else {
				System.err.println("'" + targetName + "/" + propertyName + "' was not found in the model.");
			}
		}
	}

	public static String timelineToString(Timeline timeline, Map<Object, String> targetsNamesMap) {
		String str = "";

		for (BaseTween child : timeline.getChildren()) {
			Tween tween = (Tween) child;

			str += String.format(Locale.US, "%s;%d;%d;%d;%s",
				targetsNamesMap.get(tween.getTarget()),
				tween.getType(),
				tween.getDelay(),
				tween.getDuration(),
				tween.getEasing().toString());

			for (int i=0; i<tween.getCombinedTweenCount(); i++)
				str += String.format(Locale.US, ";%f", tween.getTargetValues()[i]);

			str += "\n";
		}

		return str;
	}
}
