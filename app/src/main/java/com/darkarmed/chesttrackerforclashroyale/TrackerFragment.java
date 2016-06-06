package com.darkarmed.chesttrackerforclashroyale;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TrackerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TrackerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrackerFragment extends Fragment {
    private static final String TAG = "TrackerFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER = "user";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mUser;
    private String mParam2;

    private Context mContext;
    private GridView mGridView;
    private ChestsAdapter mAdapter;
    private List<Chest> mChests;
    private String mSequence;
    private int mCurrentChest;

    private OnFragmentInteractionListener mListener;

    public TrackerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrackerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrackerFragment newInstance(String user, String param2) {
        TrackerFragment fragment = new TrackerFragment();
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
        mSequence = getString(R.string.chest_sequence);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tracker, container, false);
        mGridView = (GridView) view.findViewById(R.id.trackerview);
        return view;
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
        mGridView.smoothScrollToPosition(mCurrentChest);

        mGridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int position = mGridView.pointToPosition((int) event.getX(), (int) event.getY());
                View view = mGridView.getChildAt(position);
                if (view != null) {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            view.setScaleX(0.9f);
                            view.setScaleY(0.9f);
                            break;
                        case MotionEvent.ACTION_UP:
                            view.setScaleX(1f);
                            view.setScaleY(1f);
                            break;
                        default:
                    }
                } else {
                    Log.i(TAG, "Touch: Out of range");
                }
                return false;
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.open(position);
                mCurrentChest = position;
            }
        });

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.lock(position);
                return true;
            }
        });
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
        String json = chestPref.getString("CHEST_SEQ", "");

        if (json.equalsIgnoreCase("")) {
            mChests = getChestList(mSequence);
            return false;
        } else {
            Log.d(TAG, json);
            mChests = new Gson().fromJson(json, new TypeToken<List<Chest>>() {}.getType());
            mCurrentChest = chestPref.getInt("CURRENT_CHEST", 0);
            return true;
        }
    }

    private boolean saveChests() {
        SharedPreferences chestPref = getActivity().getSharedPreferences(mUser, Context.MODE_PRIVATE);
        String json = new Gson().toJson(mAdapter.getItems());
        chestPref.edit().putString("CHEST_SEQ", json).putInt("CURRENT_CHEST", mCurrentChest).commit();

        Log.d(TAG, json);
        Log.d(TAG, chestPref.getString("CHEST_SEQ", ""));

        return true;
    }

    private List<Chest> getChestList(String seq) {
        List<Chest> chests = new ArrayList<>();

        for (int i = 0; i < seq.length(); ++i) {
            chests.add(new Chest(i, seq.charAt(i)));
        }

        return chests;
    }
}
