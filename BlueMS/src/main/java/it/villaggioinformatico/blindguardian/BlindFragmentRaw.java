package it.villaggioinformatico.blindguardian;

/*
 * Copyright (c) 2017  STMicroelectronics – All rights reserved
 * The STMicroelectronics corporate logo is a trademark of STMicroelectronics
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this list of conditions
 *   and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice, this list of
 *   conditions and the following disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 *
 * - Neither the name nor trademarks of STMicroelectronics International N.V. nor any other
 *   STMicroelectronics company nor the names of its contributors may be used to endorse or
 *   promote products derived from this software without specific prior written permission.
 *
 * - All of the icons, pictures, logos and other images that are provided with the source code
 *   in a directory whose title begins with st_images may only be used for internal purposes and
 *   shall not be redistributed to any third party or modified in any way.
 *
 * - Any redistributions in binary form shall not include the capability to display any of the
 *   icons, pictures, logos and other images that are provided with the source code in a directory
 *   whose title begins with st_images.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

import android.os.Bundle;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.st.BlueMS.R;
import com.st.BlueSTSDK.Feature;
import com.st.BlueSTSDK.Node;
import com.st.BlueSTSDK.gui.demos.DemoDescriptionAnnotation;

import java.util.List;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import android.widget.ImageView;

import com.st.BlueMS.demos.util.BaseDemoFragment;
import com.st.BlueSTSDK.Features.FeatureAcceleration;
import com.st.BlueSTSDK.Features.FeatureHumidity;
import com.st.BlueSTSDK.Features.FeatureLuminosity;
import com.st.BlueSTSDK.Features.FeaturePressure;
import com.st.BlueSTSDK.Features.FeatureTemperature;


/**
 * Show the Temperature, Humidity, lux and barometer values. Each value will have also its icon
 * that will change in base of the feature value
 */
@DemoDescriptionAnnotation(name="BlindGuardianDemo",iconRes=R.drawable.demo_environmental_sensor,
        requareOneOf = {FeatureHumidity.class,FeatureLuminosity.class,FeaturePressure.class,
                FeatureTemperature.class})
public class BlindFragmentRaw extends BaseDemoFragment {

    private interface ExtractDataFunction{
        float getData(Feature.Sample s);
    }

    private List<FeatureHumidity> mHumidity;
    private TextView mHumidityText;
    private ImageView mHumidityImage;
    private final static ExtractDataFunction sExtractDataHum  = FeatureHumidity::getHumidity;

    private List<FeatureTemperature> mTemperature;
    private TextView mTemperatureText;
    private ImageView mTemperatureImage;
    private final static ExtractDataFunction sExtractDataTemp  = FeatureTemperature::getTemperature;

    private List<FeaturePressure> mPressure;
    private TextView mPressureText;
    private ImageView mPressureImage;
    private final static ExtractDataFunction sExtractDataPres  = FeaturePressure::getPressure;



    public List<FeatureAcceleration> mAcceleration;
    private TextView mLuminosityText;
    private ImageView mLuminosityImage;
    private final static ExtractDataFunction sExtractDataLux  = FeatureLuminosity::getLuminosity;

    /**
     * listener for the luminosity feature, it will update the luminosity value and change the image
     * value for the {@code mLuminosityImage} image
     */
    private final Feature.FeatureListener mLuminosityListener = new Feature.FeatureListener() {
        long lastTimestamp = 0;
        double lastX = 0.0, lastY=0.0, lastZ=0.0;
        double dx = 0.0, dy=0.0, dz=0.0;
        String mx = "";

        private String getDiffValue(Feature.Sample sample, double threshold){
            String mx = "";
            if (lastX != 0.0){
                dx = lastX - FeatureAcceleration.getAccX(sample);
                if (dx > threshold){
                    mx = "" + dx;
                    lastX = FeatureAcceleration.getAccX(sample);
                }
                else {
                    mx = "" + 0;
                }
            }
            else {
                mx = "" + dx;
                lastX = FeatureAcceleration.getAccX(sample);
            }
            return mx;
        }

        @Override
        public void onUpdate(@NonNull Feature f, @NonNull Feature.Sample sample) {
            FeatureAcceleration f1 = (FeatureAcceleration) f;

            String x = "" + FeatureAcceleration.getAccX(sample);
            String y = "" + FeatureAcceleration.getAccY(sample);
            String z = "" + FeatureAcceleration.getAccZ(sample);
            String mydataString = x + " " + y+  " " + z;
            Log.d("blind", "" + FeatureAcceleration.getAccX(sample) + " " + getDiffValue(sample, 10.0));
            /*
            if (lastTimestamp == 0){
                lastTimestamp = sample.timestamp;
            }
            else {
                dataString = String.valueOf(sample.timestamp - lastTimestamp);
            }
            String timeDiff = dataString;
            String mydataString = x + " " + y+  " " + z;
            if (sample.timestamp-lastTimestamp > 6 && sample.timestamp-lastTimestamp < 10) {
                Log.d("blind", mydataString);
                lastTimestamp = sample.timestamp;
            }
            */


            updateGui(() -> {

                    try {
                        mTemperatureText.setText("X:" + x);
                        mHumidityText.setText("Y:" + y);
                        mPressureText.setText("Z: " + z);
                        mLuminosityText.setText("NA");
                    } catch (NullPointerException e) {
                        //this exception can happen when the task is run after the fragment is destroyed
                    }
            });
        }
    };

    public BlindFragmentRaw() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_eviromental_sensors, container, false);
        mHumidityText = root.findViewById(R.id.humidityText);
        mHumidityImage = root.findViewById(R.id.humidityImage);


        mTemperatureText = root.findViewById(R.id.thermometerText);
        mTemperatureImage = root.findViewById(R.id.thermometerImage);

        mPressureText = root.findViewById(R.id.barometerText);
        mPressureImage = root.findViewById(R.id.barometerImage);

        mLuminosityText = root.findViewById(R.id.luminosityText);
        mLuminosityImage = root.findViewById(R.id.luminosityImage);

        return root;
    }

    /**
     * enable the notification if the feature is not null + attach the listener for update the
     * gui when an update arrive + if you click the image we ask to read the value
     * if a feature is not available by the node a toast message is shown
     *
     * @param node node where the notification will be enabled
     */
    @Override
    protected void enableNeededNotification(@NonNull Node node) {

        mAcceleration = node.getFeatures(FeatureAcceleration.class);
        if (!mAcceleration.isEmpty()) {
            View.OnClickListener forceUpdate = new ForceUpdateFeature(mAcceleration);
            mLuminosityImage.setOnClickListener(forceUpdate);
            for (Feature f : mAcceleration) {
                f.addFeatureListener(mLuminosityListener);
                node.enableNotification(f);
            }//for
        } else {
            String message = "Non disponibile";
            updateGui(() -> mLuminosityText.setText(message));
            updateGui(() -> mLuminosityImage.setImageResource(R.drawable.illuminance_missing));
        }

    }//enableNeededNotification


    /**
     * remove the listener and disable the notification
     *
     * @param node node where disable the notification
     */
    @Override
    protected void disableNeedNotification(@NonNull Node node) {
        if(mHumidity!=null && !mHumidity.isEmpty()) {
            mHumidityImage.setOnClickListener(null);
            for (Feature f : mHumidity) {
                node.disableNotification(f);
            }//for
        }

        if(mTemperature!=null && !mTemperature.isEmpty()) {
            mTemperatureImage.setOnClickListener(null);
            for (Feature f : mTemperature) {
                node.disableNotification(f);
            }//for
        }

        if(mPressure!=null && !mPressure.isEmpty()) {
            mPressureImage.setOnClickListener(null);
            for (Feature f : mPressure) {
                node.disableNotification(f);
            }//for
        }

        if(mAcceleration !=null && !mAcceleration.isEmpty()) {
            mLuminosityImage.setOnClickListener(null);
            for (Feature f : mAcceleration) {
                f.removeFeatureListener(mLuminosityListener);
                node.disableNotification(f);
            }//for
        }

    }//disableNeedNotification

    /**
     * simple callback class that will request to read the feature value when the user click on
     * the attached
     */
    static private class ForceUpdateFeature implements View.OnClickListener {

        /**
         * feature to read
         */
        private List<? extends Feature> mFeatures;

        /**
         * @param feature feature to read when the user click on a view
         */
        ForceUpdateFeature(List<? extends Feature> feature) {
            mFeatures = feature;
        }

        /**
         * send the read request to the feature
         *
         * @param v clicked view
         */
        @Override
        public void onClick(View v) {
            for(Feature f: mFeatures) {
                Node node = f.getParentNode();
                if (node != null)
                    node.readFeature(f);
            }//if
        }//onClick

    }//ForceUpdateFeature
}

