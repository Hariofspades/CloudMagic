package com.few.cloudmagic.fragments;


import android.animation.Animator;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.few.cloudmagic.R;
import com.few.cloudmagic.interfaces.OnFragmentTouched;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * created by Hari Vignesh Jayapalan
 */
public class CircularRevealingDetailFragment extends Fragment {

    @Bind(R.id.subject)TextView sub;
    @Bind(R.id.contact)TextView cont;
    @Bind(R.id.user_name)TextView userName;
    @Bind(R.id.timeStamp)TextView timeStamp;
    @Bind(R.id.content)TextView content;
    @Bind(R.id.picture)ImageView image;
    @Bind(R.id.star)ImageButton star;
    OnFragmentTouched listener;
    View rootView;
    public static final String TAG = "INDIVIDUAL";
    RequestQueue queue;
    String subject,contacts,preview,body;

    public CircularRevealingDetailFragment() {

    }

    public static CircularRevealingDetailFragment newInstance(int centerX, int centerY, int color) {
        Bundle args = new Bundle();
        args.putInt("cx", centerX);
        args.putInt("cy", centerY);
        args.putInt("color", color);
        CircularRevealingDetailFragment fragment = new CircularRevealingDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.fragment_circular_revealing_detail, container, false);
        ButterKnife.bind(this,rootView);
        rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                       int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                int cx = getArguments().getInt("cx");
                int cy = getArguments().getInt("cy");
                // get the hypothenuse so the radius is from one corner to the other
                int radius = (int) Math.hypot(right, bottom);
                Animator reveal = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    reveal = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, radius);
                }
                assert reveal != null;
                reveal.setInterpolator(new DecelerateInterpolator(2f));
                reveal.setDuration(1000);
                reveal.start();
            }
        });
        setupBundleItems();
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (listener != null) {
                    listener.onFragmentTouched(CircularRevealingDetailFragment.this, event.getX(), event.getY());
                }
                return true;
            }
        });
        APICallList();
        return rootView;
    }

    private void setupBundleItems() {
        contacts=getArguments().getString("Contact");
        cont.setText(contacts);
        timeStamp.setText(getArguments().getString("timeStamp"));
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.valueOf(String.valueOf(contacts.charAt(0))),
                        getArguments().getInt("color"));
        image.setImageDrawable(drawable);
        userName.setText(contacts);
        star.setImageResource(getArguments().getInt("star"));
        star.setColorFilter(getArguments().getInt("tint"));

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentTouched) {
            listener = (OnFragmentTouched) activity;
        }
    }


    private int getEnclosingCircleRadius(View v, int cx, int cy) {
        int realCenterX = cx + v.getLeft();
        int realCenterY = cy + v.getTop();
        int distanceTopLeft = (int) Math.hypot(realCenterX - v.getLeft(), realCenterY - v.getTop());
        int distanceTopRight = (int) Math.hypot(v.getRight() - realCenterX, realCenterY - v.getTop());
        int distanceBottomLeft = (int) Math.hypot(realCenterX - v.getLeft(), v.getBottom() - realCenterY);
        int distanceBottomRight = (int) Math.hypot(v.getRight() - realCenterX, v.getBottom() - realCenterY);

        Integer[] distances = new Integer[]{distanceTopLeft, distanceTopRight, distanceBottomLeft,
                distanceBottomRight};

        return Collections.max(Arrays.asList(distances));
    }


    private void APICallList(){
        String url=getString(R.string.listAPI)+getArguments().getInt("ID");
        queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                       try{
                            subject=response.getString("subject");
                            preview=response.getString("preview");
                            body=response.getString("body");
                           content.setText(body);
                           sub.setText(subject);
                       }catch (Exception e){

                       }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });
        jsObjRequest.setTag(TAG);
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 1, 1));
        queue.add(jsObjRequest);
    }


}
