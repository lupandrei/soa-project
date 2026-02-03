import { TestBed } from '@angular/core/testing';

import { FaasService } from './faas.service';

describe('FaasService', () => {
  let service: FaasService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FaasService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
