package com.usingstudioo.Blocks;


import com.usingstudioo.Constants.Constants;

/**
 * Blocks provide the block declarations of different methods that can
 * be used anywhere in the project to pass as a block.
 * Generic block interface declaration for lambda expression
 */

public interface Block {
    /****************************Block Interface Declaration**********************************/
    @FunctionalInterface
    interface Finished   {
        void iFinished(boolean isFinished);
    }

    @FunctionalInterface
    interface Status     {
        void iStatus(Constants.Status iStatus);
    }

    @FunctionalInterface
    interface Success<T>    {
        void iSuccess(Constants.Status iStatus, GenericResponse<T> response);
    }

    @FunctionalInterface
    interface Failure    {
        void iFailure(Constants.Status iStatus, String error);
    }

    /**If task fail than in response send the error message, If sucessfull, then send the data in response object.*/
    @FunctionalInterface
    interface Completion<T> {
        void iCompletion(Constants.Status iStatus, GenericResponse<T> response);
    }
/****************************Block Interface Declaration**********************************/
}
