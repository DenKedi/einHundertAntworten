// Global color variables exported from SCSS
// These match the values in style.scss

export const colors = {
  // Primary Colors
  primaryBlue: '#87d2ff',
  primaryBlueHover: '#2fafff',
  primaryBlueLight: '#9cd8fd',

  // Neutral Colors
  darkGray: '#333',
  mediumGray: '#555',
  lightGray: '#b0b0b0',
  veryLightGray: '#e0e0e0',
  backgroundGray: '#f0f0f0',
  borderGray: '#ddd',
  hoverGray: 'darkgray',

  // Text Colors
  textWhite: '#ffffff',
  textDark: '#333333',

  // Background Colors
  navBg: '#333',
  questionPanelBg: '#f0f0f0',
  answerPanelBg: '#ddd',

  // Theme Colors for BleckIt Footer Banner
  bannerBgDark: 'rgba(0, 30, 80, 0.6)',
  bannerBgLight: 'rgba(240, 245, 255, 0.6)',
  bannerBorder: 'rgba(0, 102, 255, 0.2)',
  bannerBorderLight: 'rgba(0, 102, 255, 0.3)',

  // Button Colors
  btnPrimary: '#0066ff',
  btnPrimaryHover: '#0052cc',
  btnSecondary: '#ff0000',
  btnSecondaryHover: '#cc0000',
  btnAccent: '#00d4ff',
  btnAccentHover: '#00b8d4',
} as const;

export type ColorKeys = keyof typeof colors;
