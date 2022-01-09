# lamatko
Cipher contest tool that tries to break ciphers based on positional numbering systems - the core library.

# Getting started

Assume, you got a cipher based on a positional numeric system, such as a simple substitution a->1, b->2, c->3, etc.. Now, you can do:

```
Principal.solve(
            codedText = "21 17 00 19 19 4 18 4 13 00 18 08 11 13 08 02 08",
            digitDescription = "012 0123456789",
            shuffleDigitOrder = false,
            shuffleDigitCoding = false,
        ).first()
```

Lamatko will decode each word as one letter in an arbitrary-positional system, and sort results depending on how much they resemble natural language:

```
vrattesenasilniciajdetetedolevaposilnicido - -4.60338 - 012(10) 0123456789(1) - Standard
```

Lamatko is useful because it knows multiple output alphabets (like q-less or w-less) and can try all combinations of digit order or even digit coding. 
