package com.solitaire.cli;

import com.solitaire.domain.Move;
import com.solitaire.domain.Position;
import java.util.Optional;

public final class MoveParser {

    public Optional<Move> parse(String line) {
        if (line == null || line.isBlank()) return Optional.empty();

        String[] parts = line.trim().split("\\s+");
        if (parts.length != 4) return Optional.empty();

        try {
            int fr = Integer.parseInt(parts[0]);
            int fc = Integer.parseInt(parts[1]);
            int tr = Integer.parseInt(parts[2]);
            int tc = Integer.parseInt(parts[3]);

            Position from = new Position(fr, fc);
            Position to = new Position(tr, tc);

            int or = (fr + tr) / 2;
            int oc = (fc + tc) / 2;

            Position over = new Position(or, oc);

            return Optional.of(new Move(from, over, to));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
