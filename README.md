# Byte Pair Encoding Compressor

A Java implementation of byte-pair encoding (BPE) — the same general compression idea behind tools like modern LLM tokenizers — including a custom `ArrayList` built from scratch, a token map, and a file format for storing both the learned token table and the compressed output.

## Overview

Byte pair encoding repeatedly finds the most frequent adjacent pair of symbols in a sequence and replaces every occurrence of that pair with a new single "token" symbol, shrinking the sequence each pass. This project implements that idea end-to-end:

- **`ArrayList.java`** — a generic, resizable-array `List` implementation built from scratch (dynamic resizing, insert/remove/get/set, iterator) implementing a custom `SimpleList` interface.
- **`SimpleList.java`** — a trimmed-down `List` interface with default-method implementations for most of `java.util.List`'s surface, so a concrete class only needs to implement the core operations.
- **`EncodingValue.java`** — the value types stored in an encoded sequence: `ByteValue` (a raw byte) and `TokenValue` (a learned pair-token), plus a `Pair` helper for the token → (first, second) backward mapping.
- **`TokenMap.java`** — maps pairs of `EncodingValue`s to their assigned token, and tokens back to the pair they represent, backed by `EncodingValueMap` (byte values are stored in a flat 256-entry array; token values grow dynamically).
- **`EncodedList.java`** — the core algorithm: maintains a fully-encoded list of `EncodingValue`s, and after every mutation re-runs pair-merging (`createPairs`) so the list is always maximally compressed — merging the pair mapping to the *lowest* token value first, and right-to-left when tokens tie.
- **`FileHelper.java`** — serialization: writes/reads a trained token map to a `.bpet` (Byte Pair Encoding Token) file, and writes/reads a compressed document to a `.bprl` (Byte Pair Run-Length) file, which run-length-encodes runs of tokens vs. runs of raw bytes.
- **`Main.java`** — a CLI driver: loads a token map, reads an input text file, encodes it with that map, and saves both the compressed `.bprl` output and a human-readable `.bprl.sep` expansion (tokens shown pipe-delimited) for inspection.

## Project Structure

```
BytePairEncoding/
├── src/
│   ├── ArrayList.java       # Custom generic ArrayList
│   ├── SimpleList.java      # Trimmed List interface
│   ├── EncodingValue.java   # ByteValue / TokenValue / Pair
│   ├── TokenMap.java        # Pair <-> token mapping
│   ├── EncodedList.java     # Core BPE algorithm
│   ├── FileHelper.java      # .bpet / .bprl file I/O
│   └── Main.java            # CLI entry point
├── poems.bpet                # A trained token map (built from a corpus of poems)
└── README.md
```

## Running It

1. Compile: `javac src/*.java -d out`
2. Run: `java -cp out Main`
3. When prompted, provide:
   - A token map file (e.g. `poems.bpet`, included here)
   - An input text file to compress
   - An output path for the compressed `.bprl` file (a human-readable `.bprl.sep` file is also written alongside it)

## Known Gap

The original project included a corpus of example poems and their corresponding compressed (`.bprl`) and human-readable (`.bprl.sep`) outputs, used to test and demonstrate the compressor. Those example/test files aren't included here — they got scrambled badly enough during file transfer (filenames not matching their actual content) that I couldn't reliably reconstruct which encoded output belongs to which poem, and didn't want to risk shipping mismatched pairs. The core implementation above is unaffected and was verified file-by-file. If you still have the original project folder, re-adding those examples directly (rather than re-uploading them individually) would avoid the same mismatch issue.

## Author

Zach Gray
