package com.example.focusapp.Singletons;

import com.example.focusapp.Fragments.ToDoFragment;

public class ToDoFragmentSingleton {

    // static variable single_instance of type Singleton
    private static ToDoFragmentSingleton single_instance = null;

    // variable of type String
    public ToDoFragment toDoFragment;

    // private constructor restricted to this class itself
    private ToDoFragmentSingleton()
    {
        toDoFragment = new ToDoFragment();
    }

    // static method to create instance of Singleton class
    public static ToDoFragmentSingleton getInstance()
    {
        if (single_instance == null)
            single_instance = new ToDoFragmentSingleton();

        return single_instance;
    }
}
