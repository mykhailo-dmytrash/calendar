export class DateUtils {
  static getLocalTimeString(date: Date | string | undefined): string {
    if (!date) return '';
    const dateObj = typeof date === 'string' ? new Date(date) : date;
    if (isNaN(dateObj.getTime())) {
      return '';
    }
    return dateObj.toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' });
  }

  static getLocalDateString(date: Date | string | undefined): string {
    if (!date) return '';
    const dateObj = typeof date === 'string' ? new Date(date) : date;
    if (isNaN(dateObj.getTime())) {
      return '';
    }
    return dateObj.toLocaleDateString(undefined, {
      weekday: 'long',
      month: 'long',
      day: 'numeric',
      year: 'numeric'
    });
  }

  static getTimeRange(start: Date | string | undefined, end: Date | string | undefined): string {
    const startStr = this.getLocalTimeString(start);
    const endStr = this.getLocalTimeString(end);
    
    if (startStr && endStr) {
      return `${startStr} - ${endStr}`;
    } else if (startStr) {
      return startStr;
    }
    return '';
  }

  static formatDateForDisplay(date: Date | null): string {
    if (!date) return '';
    return date.toLocaleDateString(undefined, {
      weekday: 'long',
      month: 'long',
      day: 'numeric',
      year: 'numeric'
    });
  }
}


