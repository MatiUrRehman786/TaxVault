//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2019 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.sar.taxvault.utils;

import java.io.File;

public interface OutputListener {

    public void fileConverted(File file);

    void print(String output);

    void print();

    void println(String output);

    void println();

    void printError(String errorMessage);

    void printError(StackTraceElement[] stackTrace);
}
