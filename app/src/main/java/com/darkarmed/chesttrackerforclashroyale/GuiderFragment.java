package com.darkarmed.chesttrackerforclashroyale;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GuiderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GuiderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuiderFragment extends Fragment {
    private static final String TAG = "GuiderFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER = "user";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mUser;
    private String mParam2;

    private Context mContext;
    private View mView;
    private GridView mGridView;
    private ChestsAdapter mAdapter;
    private List<Chest> mChests;
    private List<ImageButton> mImageButtons;

    private OnFragmentInteractionListener mListener;

    public enum ChestButtonEnum {
        SILVER(R.id.silver_chest_button, Chest.Type.SILVER),
        GOLDEN(R.id.golden_chest_button, Chest.Type.GOLDEN),
        GIANT(R.id.giant_chest_button, Chest.Type.GIANT),
        MAGICAL(R.id.magical_chest_button, Chest.Type.MAGICAL);
//        SUPER_MAGICAL(R.id.super_magical_chest_button, Chest.Type.SUPER_MAGICAL);

        private int mViewResId;
        private Chest.Type mType;

        ChestButtonEnum(int viewResId, Chest.Type type) {
            mViewResId = viewResId;
            mType = type;
        }

        public int getViewResId() {
            return mViewResId;
        }

        public Chest.Type getType() {
            return mType;
        }
    }

    public GuiderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GuiderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GuiderFragment newInstance(String user, String param2) {
        GuiderFragment fragment = new GuiderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, user);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUser = getArguments().getString(ARG_USER);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_guider, container, false);
        mGridView = (GridView) mView.findViewById(R.id.guiderview);
        return mView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        loadChests();
        mAdapter = new ChestsAdapter(mContext, mChests);
        mGridView.setAdapter(mAdapter);
        mGridView.smoothScrollToPosition(mGridView.getCount() - 1);

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.remove(position);
                return true;
            }
        });

        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Supercell-Magic_5.ttf");

        TextView matchedTextView = (TextView) mView.findViewById(R.id.matched_text_view);
        matchedTextView.setText(getString(R.string.matched_position));
        matchedTextView.setTypeface(tf);

        TextView matchedPosTextView = (TextView) mView.findViewById(R.id.matched_position_view);
        matchedPosTextView.setText("None");
//        matchedPosTextView.setText("9 37 53");
        matchedPosTextView.setTypeface(tf);

        Button applyButton = (Button) mView.findViewById(R.id.matched_apply_button);
        applyButton.setTypeface(tf);

        for (ChestButtonEnum e : ChestButtonEnum.values()) {
            final ImageButton imageButton = (ImageButton) mView.findViewById(e.getViewResId());
            imageButton.setTag(e.getType());
            imageButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            imageButton.setScaleX(0.9f);
                            imageButton.setScaleY(0.9f);
                            break;
                        case MotionEvent.ACTION_UP:
                            imageButton.setScaleX(1f);
                            imageButton.setScaleY(1f);
                            break;
                        default:
                    }
                    return false;
                }
            });

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addChest((Chest.Type) v.getTag());
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveChests();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private boolean loadChests() {
        SharedPreferences chestPref = getActivity().getSharedPreferences(mUser, Context.MODE_PRIVATE);
        String json = chestPref.getString("CHESTS", "");

        if (json.equalsIgnoreCase("")) {
            mChests = new ArrayList<>();
            return false;
        } else {
            Log.d(TAG, json);
            mChests = new Gson().fromJson(json, new TypeToken<List<Chest>>() {}.getType());
            return true;
        }
    }

    private boolean saveChests() {
        SharedPreferences chestPref = getActivity().getSharedPreferences(mUser, Context.MODE_PRIVATE);
        String json = new Gson().toJson(mAdapter.getItems());
        chestPref.edit().putString("CHESTS", json).commit();

        Log.d(TAG, json);
        Log.d(TAG, chestPref.getString("CHESTS", ""));

        return true;
    }

    private void addChest(Chest.Type type) {
        if (mAdapter != null){
            mAdapter.add(new Chest(mAdapter.getCount(), type, Chest.Status.OPENED));
            mGridView.smoothScrollToPosition(mGridView.getCount() - 1);
        }
    }
}
