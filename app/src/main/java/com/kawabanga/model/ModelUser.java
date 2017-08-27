package com.kawabanga.model;

public class ModelUser {

    public static ModelUser instance = new ModelUser();
    private FirebaseUser firebaseUser;

    //constructor
    private ModelUser() {
        firebaseUser = new FirebaseUser();

    }

    public interface GetUserCallback {
        void onComplete(User user);

        void onCancel();
    }

    public void getUser(String userId, final GetUserCallback callback) {
        firebaseUser.getUser(userId, new FirebaseUser.GetUserCallback() {
            @Override
            public void onComplete(User user) {
                callback.onComplete(user);
            }

            @Override
            public void onCancel() {
                onCancel();
            }
        });

    }

    public void addUser(User user) {
        firebaseUser.addUser(user);
    }

}
