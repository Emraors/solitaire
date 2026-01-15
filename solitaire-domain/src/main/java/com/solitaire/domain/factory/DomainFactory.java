package com.solitaire.domain.factory;

import com.solitaire.domain.Board;
import com.solitaire.domain.Cell;
import com.solitaire.domain.rules.EnglishRules;

public class DomainFactory {

	public static DomainObjects buildDomain() {
		return new DomainObjects(englishBoard(), new EnglishRules());
	}


	private static Board englishBoard() {
		Cell[][] cells = new Cell[7][7];

		for (int r = 0; r < 7; r++) {
			for (int c = 0; c < 7; c++) {
				cells[r][c] = isEnglishValidHole(r, c) ? Cell.PEG : Cell.INVALID;
			}
		}

		cells[3][3] = Cell.EMPTY;

		return new Board(cells);
	}

	private static boolean isEnglishValidHole(int r, int c) {
		boolean top = r <= 1;
		boolean bottom = r >= 5;
		boolean left = c <= 1;
		boolean right = c >= 5;

		// 2x2 corner blocks are invalid
		boolean inCornerBlock = (top || bottom) && (left || right);
		return !inCornerBlock;
	}
}
