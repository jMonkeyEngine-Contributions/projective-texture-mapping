/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jme3test.ext.projectivetexturemapping;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.ext.projectivetexturemapping.AnimatedTextureProjectorRenderer;
import com.jme3.ext.projectivetexturemapping.SimpleTextureProjector;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.GeometryList;
import com.jme3.renderer.queue.OpaqueComparator;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireFrustum;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;

/**
 * Test application for Projective Texture Mapping.
 * @author survivor, H
 */
public class TestProjectiveAnimatedTextureMapping extends SimpleApplication 
{
  private ProjectorData pd2;
  private AnimatedTextureProjectorRenderer aptr;

  public static void main(final String[] args) {
    final TestProjectiveAnimatedTextureMapping app = new TestProjectiveAnimatedTextureMapping();
    app.start();
    Logger.getLogger("").setLevel(Level.SEVERE);
  }

  @Override
  public void simpleInitApp() 
  {
    setPauseOnLostFocus(false);
    this.flyCam.setMoveSpeed(3f);

    final Material mat = new Material(this.assetManager, "Common/MatDefs/Light/Lighting.j3md");
    mat.setColor("Diffuse", ColorRGBA.LightGray);
    mat.setColor("Ambient", ColorRGBA.LightGray);
    mat.setBoolean("UseMaterialColors", true);

    final Box box1 = new Box(Vector3f.ZERO.clone(), 10f, 0.1f, 10f);
    final Geometry geom1 = new Geometry("Box1", box1);
    geom1.setMaterial(mat);
    this.rootNode.attachChild(geom1);

    final Material mat2 = new Material(this.assetManager, "Common/MatDefs/Light/Lighting.j3md");
    mat2.setColor("Diffuse", ColorRGBA.Orange);
    mat.setColor("Ambient", ColorRGBA.LightGray);
    mat2.setBoolean("UseMaterialColors", true);

    final Sphere sphere1 = new Sphere(32, 32, 0.5f);
    final Geometry geom2 = new Geometry("Sphere1", sphere1);
    geom2.setMaterial(mat2);
    this.rootNode.attachChild(geom2);

    DirectionalLight dl = new DirectionalLight();
    dl.setDirection(new Vector3f(-0.1f, -0.7f, 1).normalizeLocal());
    dl.setColor(new ColorRGBA(0.44f, 0.30f, 0.20f, 1.0f));
    this.rootNode.addLight(dl);

    // skylight
    dl = new DirectionalLight();
    dl.setDirection(new Vector3f(-0.6f, -1, -0.6f).normalizeLocal());
    dl.setColor(new ColorRGBA(0.10f, 0.22f, 0.44f, 1.0f));
    this.rootNode.addLight(dl);

    // white ambient light
    dl = new DirectionalLight();
    dl.setDirection(new Vector3f(1, -0.5f, -0.1f).normalizeLocal());
    dl.setColor(new ColorRGBA(0.80f, 0.70f, 0.80f, 1.0f));
    this.rootNode.addLight(dl);

    // real ambient light
    final AmbientLight al = new AmbientLight();
    al.setColor(new ColorRGBA(0.1f, 0.1f, 0.1f, 1.0f));
    this.rootNode.addLight(al);

    final float ar = (float) this.settings.getWidth() / (float) this.settings.getHeight();
    this.cam.setFrustumPerspective(45, ar, 0.1f, 1000.0f);
    this.cam.setLocation(new Vector3f(-1, 3, -1));
    this.cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y.clone());

    final Texture2D texture2 = (Texture2D) this.assetManager.loadTexture("Textures/simbolo1 animado.png");

    this.pd2 = new ProjectorData();
    initProjectorData(this.pd2, new Vector3f(1f, 2.1f, 2f), texture2);

    final GeometryList gl = new GeometryList(new OpaqueComparator());
    gl.add(geom1);
    this.pd2.projector.setTargetGeometryList(gl);
    this.pd2.projector.getProjectorCamera().setFrustumPerspective(90f, 1f, 1f, 5f);
    this.pd2.projector.getProjectorCamera().setParallelProjection(true);

    this.aptr = new AnimatedTextureProjectorRenderer(this.assetManager, this.timer, 6, 1, 20);
    this.aptr.getTextureProjectors().add(this.pd2.projector);

    Logger.getLogger("").severe(
            "NUM_PROJECTORS: " + this.aptr.getTextureProjectors().size() + ", NUM_PASSES: "
            + this.aptr.getTextureProjectors().size());

    this.viewPort.addProcessor(this.aptr);
  }

  private void initProjectorData(final ProjectorData pd, final Vector3f location, final Texture2D texture) 
  {
    texture.setMinFilter(Texture.MinFilter.Trilinear);
    texture.setMagFilter(Texture.MagFilter.Bilinear);
    texture.setAnisotropicFilter(16);
    // texture.setWrap(Texture.WrapMode.EdgeClamp);
    texture.setWrap(Texture.WrapMode.BorderClamp);

    final int textureWidth = texture.getImage().getWidth();
    final int textureHeight = texture.getImage().getHeight();
    final float textureAspectRatio = (float) textureWidth / (float) textureHeight;

    pd.projector = new SimpleTextureProjector(texture);
    final Camera projectorCamera = pd.projector.getProjectorCamera();
    projectorCamera.setLocation(location);
    projectorCamera.lookAt(Vector3f.ZERO.clone(), Vector3f.UNIT_X.clone());
    projectorCamera.setFrustumPerspective(45, textureAspectRatio, 1f, 5f);
    projectorCamera.setParallelProjection(false);

    pd.frustumPoints = new Vector3f[8];
    for (int i = 0; i < 8; i++) {
      pd.frustumPoints[i] = new Vector3f();
    }

    pd.frustum = new WireFrustum(pd.frustumPoints);
    final Geometry frustumMdl = new Geometry("f", pd.frustum);
    frustumMdl.setCullHint(Spatial.CullHint.Never);
    frustumMdl.setShadowMode(ShadowMode.Off);
    frustumMdl.setMaterial(new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md"));
    frustumMdl.getMaterial().setColor("Color", ColorRGBA.White);
    this.rootNode.attachChild(frustumMdl);
  }

  @Override
  public void simpleUpdate(final float tpf) 
  {
    final float s = FastMath.sin(this.timer.getTimeInSeconds() * 0.8f - FastMath.PI)
            * FastMath.sin(this.timer.getTimeInSeconds() * 0.5f - FastMath.PI);
    final float t = FastMath.cos(this.timer.getTimeInSeconds() * 0.6f - FastMath.PI)
            * FastMath.sin(this.timer.getTimeInSeconds() * 0.3f - FastMath.PI);

    this.pd2.projector.getProjectorCamera().lookAtDirection(Vector3f.UNIT_Y.negate(), Vector3f.UNIT_X.clone());
    this.pd2.projector.getProjectorCamera().setLocation(new Vector3f(t * 2f, 2.1f, s * 2f));

    this.pd2.projector.updateFrustumPoints(this.pd2.frustumPoints);
    this.pd2.frustum.update(this.pd2.frustumPoints);
  }

  private class ProjectorData 
  {
    SimpleTextureProjector projector;
    WireFrustum frustum;
    Vector3f[] frustumPoints;
  }
}
