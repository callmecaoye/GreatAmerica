/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.promeets.android.util;

public interface AuthenticationCallback<T> {
    void onSuccess(T data);
    void onError(Exception e);
}
