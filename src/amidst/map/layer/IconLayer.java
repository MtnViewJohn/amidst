package amidst.map.layer;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import amidst.map.Fragment;
import amidst.map.Map;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.World;
import amidst.minecraft.world.object.WorldObject;
import amidst.minecraft.world.object.WorldObjectProducer;
import amidst.preferences.BooleanPrefModel;

public class IconLayer extends Layer {
	private final AffineTransform iconLayerMatrix = new AffineTransform();
	private final BooleanPrefModel isVisiblePreference;
	private final WorldObjectProducer producer;

	public IconLayer(World world, Map map, LayerType layerType,
			BooleanPrefModel isVisiblePreference, WorldObjectProducer producer) {
		super(world, map, layerType);
		this.isVisiblePreference = isVisiblePreference;
		this.producer = producer;
	}

	@Override
	public boolean isVisible() {
		return isVisiblePreference.get();
	}

	@Override
	public void load(Fragment fragment) {
		doLoad(fragment);
	}

	@Override
	public void reload(Fragment fragment) {
		doLoad(fragment);
	}

	protected void doLoad(Fragment fragment) {
		fragment.putWorldObjects(layerType,
				producer.getAt(fragment.getCorner()));
	}

	@Override
	public void draw(Fragment fragment, Graphics2D g2d,
			AffineTransform layerMatrix) {
		double invZoom = 1.0 / map.getZoom();
		for (WorldObject worldObject : fragment.getWorldObjects(layerType)) {
			drawObject(worldObject, invZoom, g2d, layerMatrix);
		}
	}

	private void drawObject(WorldObject worldObject, double invZoom,
			Graphics2D g2d, AffineTransform layerMatrix) {
		BufferedImage image = worldObject.getImage();
		int width = image.getWidth();
		int height = image.getHeight();
		if (map.getSelectedWorldObject() == worldObject) {
			width *= 1.5;
			height *= 1.5;
		}
		initIconLayerMatrix(invZoom, worldObject.getCoordinates(), layerMatrix);
		g2d.setTransform(iconLayerMatrix);
		g2d.drawImage(image, -(width >> 1), -(height >> 1), width, height, null);
	}

	private void initIconLayerMatrix(double invZoom,
			CoordinatesInWorld coordinates, AffineTransform layerMatrix) {
		iconLayerMatrix.setTransform(layerMatrix);
		iconLayerMatrix.translate(coordinates.getXRelativeToFragment(),
				coordinates.getYRelativeToFragment());
		iconLayerMatrix.scale(invZoom, invZoom);
	}
}
